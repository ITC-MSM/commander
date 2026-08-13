package com.wingedsheep.engine.handlers.effects.zones

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.TargetResolutionUtils
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.GraveyardEntryTurnComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.scripting.effects.ReturnCreaturesPutInGraveyardThisTurnEffect
import com.wingedsheep.sdk.scripting.effects.CompositeEffect
import com.wingedsheep.sdk.scripting.effects.MoveToZoneEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import kotlin.reflect.KClass

/**
 * Executor for ReturnCreaturesPutInGraveyardThisTurnEffect.
 *
 * Finds all creature cards in the resolved player's graveyard that have a
 * GraveyardEntryTurnComponent matching the current turn number, and returns
 * them to their owner's hand.
 */
class ReturnCreaturesPutInGraveyardThisTurnExecutor(
    private val recurse: (GameState, com.wingedsheep.sdk.scripting.effects.Effect, EffectContext) -> EffectResult
) : EffectExecutor<ReturnCreaturesPutInGraveyardThisTurnEffect> {

    override val effectType: KClass<ReturnCreaturesPutInGraveyardThisTurnEffect> =
        ReturnCreaturesPutInGraveyardThisTurnEffect::class

    override fun execute(
        state: GameState,
        effect: ReturnCreaturesPutInGraveyardThisTurnEffect,
        context: EffectContext
    ): EffectResult {
        val playerId = resolvePlayer(effect.player, context, state)
        val graveyardKey = ZoneKey(playerId, Zone.GRAVEYARD)
        val graveyardIds = state.getZone(graveyardKey)

        // Find creature cards put into graveyard this turn
        val creaturesToReturn = graveyardIds.filter { entityId ->
            val container = state.getEntity(entityId) ?: return@filter false
            val cardComponent = container.get<CardComponent>() ?: return@filter false
            val entryTurn = container.get<GraveyardEntryTurnComponent>() ?: return@filter false
            cardComponent.typeLine.isCreature && entryTurn.turnNumber == state.turnNumber
        }

        if (creaturesToReturn.isEmpty()) {
            return EffectResult.success(state)
        }

        // Freeze the eligible set at resolution time, then delegate each physical move to the
        // generic move effect.  CompositeEffect's continuation sits below a paused Commander
        // replacement decision, so a CR 903.9b answer completes exactly one move before the
        // next card is considered.  This replaces the former direct remove/add bypass, which
        // could silently put a commander into hand without offering its owner the replacement.
        return recurse(
            state,
            CompositeEffect(creaturesToReturn.map { entityId ->
                MoveToZoneEffect(
                    target = EffectTarget.SpecificEntity(entityId),
                    destination = Zone.HAND,
                    fromZone = Zone.GRAVEYARD
                )
            }),
            context
        )
    }

    private fun resolvePlayer(player: Player, context: EffectContext, state: GameState) =
        TargetResolutionUtils.resolvePlayerRef(player, context, state) ?: context.controllerId
}
