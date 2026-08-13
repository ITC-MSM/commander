package com.wingedsheep.engine.handlers.continuations

import com.wingedsheep.engine.core.ActivatedAbilityPostPaymentContinuation
import com.wingedsheep.engine.core.ActivatedAbilityReturnToHandRemainderContinuation
import com.wingedsheep.engine.core.ExecutionResult
import com.wingedsheep.engine.core.GameEvent
import com.wingedsheep.engine.core.EngineServices
import com.wingedsheep.engine.event.TriggerDetector
import com.wingedsheep.engine.event.TriggerProcessor
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService

/** Finishes an activation after its final Commander-replaceable cost has physically moved. */
class ActivatedAbilityPostPaymentContinuationResumer(
    services: EngineServices
) : AutoResumerModule {
    private val stackResolver = services.stackResolver
    private val triggerDetector = services.triggerDetector
    private val triggerProcessor = services.triggerProcessor

    override fun autoResumers(): List<AutoResumer<*>> = listOf(
        autoResumer(ActivatedAbilityReturnToHandRemainderContinuation::class) { state, continuation, events, checkForMore ->
            resumeRemainingReturns(state, continuation, events, checkForMore)
        },
        autoResumer(ActivatedAbilityPostPaymentContinuation::class) { state, continuation, events, checkForMore ->
            val stack = stackResolver.putActivatedAbility(
                state,
                continuation.abilityOnStack,
                continuation.targets,
                targetRequirements = continuation.targetRequirements,
                costsTap = continuation.costsTap,
                isExhaust = continuation.isExhaust,
                cantBeCopied = continuation.cantBeCopied
            )
            val emitted = events + stack.events
            val triggers = triggerDetector.detectTriggers(stack.newState, continuation.costEvents + emitted)
            if (triggers.isNotEmpty()) {
                val processed = triggerProcessor.processTriggers(stack.newState, triggers)
                if (processed.isPaused) {
                    ExecutionResult.paused(
                        processed.state.withPriority(continuation.controllerId),
                        processed.pendingDecision!!,
                        emitted + processed.events
                    )
                } else {
                    ExecutionResult.success(
                        processed.newState.withPriority(continuation.controllerId),
                        emitted + processed.events
                    )
                }
            } else {
                ExecutionResult.success(stack.newState, emitted)
            }
        }
    )

    private fun resumeRemainingReturns(
        state: GameState,
        continuation: ActivatedAbilityReturnToHandRemainderContinuation,
        events: List<GameEvent>,
        checkForMore: CheckForMore
    ): ExecutionResult {
        val next = continuation.remainingPermanentIds.firstOrNull()
            ?: return checkForMore(state.pushContinuation(continuation.tail), events)
        val attempt = ZoneTransitionService.attemptMoveToZone(state, next, com.wingedsheep.sdk.core.Zone.HAND)
        val allEvents = events + attempt.events
        val remaining = continuation.remainingPermanentIds.drop(1)
        if (!attempt.isPaused) {
            return resumeRemainingReturns(
                attempt.state,
                continuation.copy(remainingPermanentIds = remaining),
                allEvents,
                checkForMore
            )
        }
        val stack = attempt.state.continuationStack
        val remainder = continuation.copy(remainingPermanentIds = remaining)
        val paused = attempt.state.copy(continuationStack = stack.dropLast(1) + remainder + stack.last())
        return ExecutionResult.paused(paused, attempt.pendingDecision!!, allEvents)
    }
}
