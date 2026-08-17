package com.wingedsheep.engine.handlers.effects.mana

import com.wingedsheep.engine.core.EngineServices
import com.wingedsheep.engine.core.ExecutionResult
import com.wingedsheep.engine.core.GameEvent
import com.wingedsheep.engine.core.ManaAddedEvent
import com.wingedsheep.engine.handlers.DynamicAmountEvaluator
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.PredicateContext
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AdditionalManaOnSourceTap
import com.wingedsheep.sdk.scripting.TappedForManaType

/**
 * Applies [AdditionalManaOnSourceTap] bonuses for a tap whose produced color is only known after a
 * color choice resolves.
 *
 * `ActivateAbilityHandler` resolves these inline (`resolveAdditionalManaOnSourceTap`) right after a
 * mana ability's effect executes — but only when that effect *didn't* pause. An "add one mana of
 * any color" producer (Roxanne's Meteorite; anything holding a Cryptolith Rite-style grant) pauses
 * for the color decision and returns before reaching that code, so the whole bonus pass is skipped.
 * The color-choice continuation resumer calls this once the produced color is known, which is the
 * only place the two forms can be told apart:
 *
 * - **Mirror** (`color = null`) — Roxanne, Starfall Savant's "add one mana of any type that artifact
 *   token produced". Needs the chosen color; can only be resolved here.
 * - **Fixed color** (`color != null`) — Badgermole Cub's "Whenever you tap a creature for mana, add
 *   an additional {G}". Doesn't depend on the produced color, but is lost all the same when the
 *   producer pauses, because the inline pass never ran.
 *
 * The two paths are mutually exclusive — the inline pass runs only when the effect completed
 * synchronously — so nothing is applied twice.
 *
 * Returns the accumulated mana-added events for the bonus.
 */
object AdditionalManaOnSourceTapOnColorChoice {

    private val dynamicAmountEvaluator = DynamicAmountEvaluator()

    fun applyForResolvedTap(
        services: EngineServices,
        state: GameState,
        sourceId: EntityId?,
        tappingPlayerId: EntityId,
        producedColor: Color,
    ): ExecutionResult {
        if (sourceId == null) return ExecutionResult.success(state)
        val predicateEvaluator = services.predicateEvaluator
        var currentState = state
        val events = mutableListOf<GameEvent>()

        for (entityId in currentState.getBattlefield()) {
            val container = currentState.getEntity(entityId) ?: continue
            val card = container.get<CardComponent>() ?: continue
            val cardDef = services.cardRegistry.getCard(card.cardDefinitionId) ?: continue
            for (staticAbility in cardDef.script.staticAbilities) {
                val onSourceTap = staticAbility as? AdditionalManaOnSourceTap ?: continue
                // This resume path only fires after a *color* choice resolves, so the produced mana
                // is always colored — a COLORLESS-gated bonus ("tap for {C}") can never match here.
                if (onSourceTap.whenProducing == TappedForManaType.COLORLESS) continue

                val staticController = currentState.projectedState.getController(entityId) ?: continue
                val filterContext = PredicateContext(controllerId = staticController, sourceId = entityId)
                if (!predicateEvaluator.matches(
                        currentState, currentState.projectedState, sourceId, onSourceTap.sourceFilter, filterContext
                    )) continue

                val effectContext = EffectContext(
                    sourceId = entityId,
                    controllerId = tappingPlayerId,
                    targets = emptyList(),
                    xValue = null
                )
                val bonusAmount = dynamicAmountEvaluator.evaluate(currentState, onSourceTap.amount, effectContext)
                if (bonusAmount <= 0) continue

                // Explicit color wins; null mirrors the color that was just chosen.
                val bonusColor = onSourceTap.color ?: producedColor

                currentState = currentState.updateEntity(tappingPlayerId) { c ->
                    val pool = c.get<ManaPoolComponent>() ?: ManaPoolComponent()
                    c.with(pool.add(bonusColor, bonusAmount))
                }
                events.add(
                    ManaAddedEvent(
                        playerId = tappingPlayerId,
                        sourceId = entityId,
                        sourceName = card.name,
                        white = if (bonusColor == Color.WHITE) bonusAmount else 0,
                        blue = if (bonusColor == Color.BLUE) bonusAmount else 0,
                        black = if (bonusColor == Color.BLACK) bonusAmount else 0,
                        red = if (bonusColor == Color.RED) bonusAmount else 0,
                        green = if (bonusColor == Color.GREEN) bonusAmount else 0,
                        colorless = 0
                    )
                )
            }
        }
        return ExecutionResult.success(currentState, events)
    }
}
