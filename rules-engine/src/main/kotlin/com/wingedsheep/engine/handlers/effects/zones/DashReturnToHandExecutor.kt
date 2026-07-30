package com.wingedsheep.engine.handlers.effects.zones

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.battlefield.BattlefieldEntryTimestampComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.scripting.effects.DashReturnToHandEffect
import kotlin.reflect.KClass

/**
 * Executor for [DashReturnToHandEffect].
 *
 * Returns a dashed permanent to its owner's hand (CR 702.109a's second clause). Per the official
 * ruling, this only happens if the permanent is still on the battlefield when the delayed
 * trigger resolves — if it died or otherwise left the battlefield before then, it stays wherever
 * it went instead, so a missing/absent target is a silent no-op rather than an error.
 *
 * Used by the dash mechanic's delayed trigger that fires at the beginning of the next end step.
 */
class DashReturnToHandExecutor : EffectExecutor<DashReturnToHandEffect> {

    override val effectType: KClass<DashReturnToHandEffect> = DashReturnToHandEffect::class

    override fun execute(
        state: GameState,
        effect: DashReturnToHandEffect,
        context: EffectContext
    ): EffectResult {
        val targetId = context.resolveTarget(effect.target, state)
            ?: return EffectResult.success(state) // Permanent may have already left the battlefield

        val container = state.getEntity(targetId)
            ?: return EffectResult.success(state)

        container.get<CardComponent>()
            ?: return EffectResult.success(state)

        // Only return it if the permanent is still on the battlefield.
        if (targetId !in state.getBattlefield()) return EffectResult.success(state)

        // CR 603.7c: the delayed trigger tracks the specific object that was dashed in. A
        // permanent that left the battlefield and returned (blink) before this fires is a new
        // object (CR 400.7) the trigger must not return — detect that via the battlefield-entry
        // timestamp snapshotted when the trigger was created.
        if (effect.enteredBattlefieldTimestamp != null) {
            val currentEntry = container
                .get<BattlefieldEntryTimestampComponent>()
                ?.timestamp
            if (currentEntry != effect.enteredBattlefieldTimestamp) {
                return EffectResult.success(state)
            }
        }

        // Use ZoneTransitionService for proper cleanup (strip battlefield components — including
        // DashedComponent, so a same-named recast later doesn't start with stale haste — etc.).
        val transitionResult = ZoneTransitionService.moveToZone(
            state = state,
            entityId = targetId,
            destinationZone = Zone.HAND
        )

        return EffectResult.success(transitionResult.state, transitionResult.events)
    }
}
