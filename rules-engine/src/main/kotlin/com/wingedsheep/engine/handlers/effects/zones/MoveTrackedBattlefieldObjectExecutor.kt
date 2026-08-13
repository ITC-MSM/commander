package com.wingedsheep.engine.handlers.effects.zones

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.handlers.effects.ZoneTransitionAttemptResult
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.battlefield.BattlefieldEntryTimestampComponent
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.MoveTrackedBattlefieldObjectEffect
import kotlin.reflect.KClass

/**
 * Executor for timestamp-guarded movement of one battlefield object.
 */
class MoveTrackedBattlefieldObjectExecutor : EffectExecutor<MoveTrackedBattlefieldObjectEffect> {

    override val effectType: KClass<MoveTrackedBattlefieldObjectEffect> =
        MoveTrackedBattlefieldObjectEffect::class

    override fun execute(
        state: GameState,
        effect: MoveTrackedBattlefieldObjectEffect,
        context: EffectContext
    ): EffectResult {
        val targetId = context.resolveTarget(effect.target, state)
            ?: return EffectResult.success(state)
        val transitionResult = moveTrackedBattlefieldObject(
            state,
            targetId,
            effect.destination,
            effect.enteredBattlefieldTimestamp
        ) ?: return EffectResult.success(state)

        return if (transitionResult.isPaused) {
            EffectResult.paused(
                transitionResult.state,
                transitionResult.pendingDecision!!,
                transitionResult.events
            )
        } else {
            EffectResult.success(transitionResult.state, transitionResult.events)
        }
    }
}

/**
 * Move [targetId] only while it still represents the battlefield object captured by
 * [enteredBattlefieldTimestamp]. Entity IDs survive zone changes in this engine, so the timestamp
 * is what distinguishes a blinked permanent from the object an earlier delayed trigger tracked.
 */
internal fun moveTrackedBattlefieldObject(
    state: GameState,
    targetId: EntityId,
    destination: Zone,
    enteredBattlefieldTimestamp: Long?
): ZoneTransitionAttemptResult? {
    val container = state.getEntity(targetId) ?: return null
    if (targetId !in state.getBattlefield()) return null
    if (enteredBattlefieldTimestamp != null) {
        val currentEntry = container.get<BattlefieldEntryTimestampComponent>()?.timestamp
        if (currentEntry != enteredBattlefieldTimestamp) return null
    }
    // This executor performs no work after the zone move, so it can safely
    // propagate a pause from the generic CR 614–616 replacement pipeline.
    // Multi-step executors must instead carry their remaining work in a
    // continuation before adopting attemptMoveToZone.
    return ZoneTransitionService.attemptMoveToZone(
        state = state,
        entityId = targetId,
        destinationZone = destination
    )
}
