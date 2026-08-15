package com.wingedsheep.engine.handlers.effects.permanent.counters

import com.wingedsheep.engine.core.CountersAddedEvent
import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.DamageUtils
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.handlers.effects.ReplacementEffectUtils
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.sdk.scripting.effects.MoveAllLastKnownCountersEffect
import kotlin.reflect.KClass

/**
 * Executor for [MoveAllLastKnownCountersEffect].
 *
 * Reads the last-known counter map captured when the trigger's source left the
 * battlefield ([EffectContext.triggerLastKnownCounters]) and places one of every
 * counter kind onto the target. Used by Essence Channeler:
 * "When this creature dies, put its counters on target creature you control."
 *
 * Per the Bloomburrow ruling: this moves *all* counter types, not just +1/+1.
 */
class MoveAllLastKnownCountersExecutor : EffectExecutor<MoveAllLastKnownCountersEffect> {

    override val effectType: KClass<MoveAllLastKnownCountersEffect> =
        MoveAllLastKnownCountersEffect::class

    override fun execute(
        state: GameState,
        effect: MoveAllLastKnownCountersEffect,
        context: EffectContext
    ): EffectResult {
        val targetId = context.resolveTarget(effect.target, state)
            ?: return EffectResult.error(state, "No valid target for moved counters")

        // Dies/leaves triggers carry the last-known counter map on triggerLastKnownCounters; an
        // activated ability whose source was sacrificed/exiled as a cost (Zack Fair) carries the
        // same map on lastKnownSourceCounters (captured before the cost wiped them, CR 112.7a). Read
        // the trigger map first, falling back to the cost-sacrifice map so both shapes work.
        val lastKnown = context.triggerLastKnownCounters?.takeIf { it.isNotEmpty() }
            ?: context.lastKnownSourceCounters
        if (lastKnown.isEmpty()) {
            return EffectResult.success(state, emptyList())
        }

        if (!state.projectedState.canReceiveCounters(targetId)) {
            return EffectResult.success(state, emptyList())
        }

        var newState = state
        val events = mutableListOf<com.wingedsheep.engine.core.GameEvent>()
        val targetName = state.getEntity(targetId)?.get<CardComponent>()?.name ?: ""
        // One "put counters" event batch on a single creature ⇒ one "first this turn" flag,
        // carried on the first emitted event so it fires the intervening-if trigger once.
        var firstThisTurn = DamageUtils.isFirstCounterThisTurn(state, targetId)

        for ((counterTypeString, count) in lastKnown) {
            if (count <= 0) continue
            val counterType = resolveCounterType(counterTypeString)

            val modifiedCount = ReplacementEffectUtils.applyCounterPlacementModifiers(
                newState, targetId, counterType, count, placerId = context.controllerId
            )
            if (modifiedCount <= 0) continue

            val current = newState.getEntity(targetId)?.get<CountersComponent>() ?: CountersComponent()
            newState = newState.updateEntity(targetId) { container ->
                container.with(current.withAdded(counterType, modifiedCount))
            }
            events.add(CountersAddedEvent(targetId, counterTypeString, modifiedCount, targetName, firstThisTurn, placedBy = context.controllerId))
            // Per kind, inside the loop — see DoubleCountersExecutor: the marker records which
            // kinds landed, so a kind-less mark would not satisfy a type-scoped counter-history
            // filter.
            newState = DamageUtils.markCounterPlacedOnCreature(
                newState, context.controllerId, targetId, counterTypeString
            )
            firstThisTurn = false
        }

        return EffectResult.success(newState, events)
    }
}
