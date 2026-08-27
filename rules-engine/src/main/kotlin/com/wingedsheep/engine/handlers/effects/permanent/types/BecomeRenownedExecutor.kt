package com.wingedsheep.engine.handlers.effects.permanent.types

import com.wingedsheep.engine.core.BecameRenownedEvent
import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.battlefield.RenownedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.sdk.scripting.effects.BecomeRenownedEffect
import kotlin.reflect.KClass

/**
 * Executor for [BecomeRenownedEffect] — the designation half of a renown trigger (CR 702.112a).
 * Stamps the [RenownedComponent] marker and emits a [BecameRenownedEvent].
 *
 * "Renowned" is a designation (CR 702.112b), not an ability and not a copiable value: nothing about
 * the creature's characteristics changes here. Renown payoffs read the marker back on their own
 * through `Conditions.SourceIsRenowned`.
 *
 * Renowning an already-renowned permanent is a no-op that emits no event — the designation is
 * sticky and one-way, so a second application has nothing to report. Renown's own intervening-`if`
 * normally prevents that case; this guard is what makes CR 702.112c's second instance harmless if
 * the two abilities ever resolve back to back without the `if` being re-tested.
 */
class BecomeRenownedExecutor : EffectExecutor<BecomeRenownedEffect> {

    override val effectType: KClass<BecomeRenownedEffect> = BecomeRenownedEffect::class

    override fun execute(
        state: GameState,
        effect: BecomeRenownedEffect,
        context: EffectContext
    ): EffectResult {
        val targetId = context.resolveTarget(effect.target)
            ?: return EffectResult.success(state)

        // CR 702.112b — only permanents can be or become renowned. A creature that left the
        // battlefield in response to its own renown trigger simply does nothing.
        if (targetId !in state.getBattlefield()) {
            return EffectResult.success(state)
        }

        val container = state.getEntity(targetId) ?: return EffectResult.success(state)
        if (container.has<RenownedComponent>()) {
            return EffectResult.success(state)
        }

        val name = container.get<CardComponent>()?.name ?: "Unknown"
        // Read from the projection so a creature under someone else's control credits that player
        // — "whenever a creature you control becomes renowned" (Valeron Wardens) matches on it.
        val controller = state.projectedState.getController(targetId)
            ?: container.get<ControllerComponent>()?.playerId
            ?: context.controllerId
        val newState = state.updateEntity(targetId) { it.with(RenownedComponent) }

        return EffectResult.success(newState, listOf(BecameRenownedEvent(targetId, name, controller)))
    }
}
