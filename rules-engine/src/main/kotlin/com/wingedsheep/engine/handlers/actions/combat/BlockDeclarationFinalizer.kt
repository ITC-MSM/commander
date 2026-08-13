package com.wingedsheep.engine.handlers.actions.combat

import com.wingedsheep.engine.core.BlockDeclarationSbaBoundaryContinuation
import com.wingedsheep.engine.core.BlockDeclarationPostPlacementContinuation
import com.wingedsheep.engine.core.DeferredBlockTriggersContinuation
import com.wingedsheep.engine.core.ExecutionResult
import com.wingedsheep.engine.core.GameEvent
import com.wingedsheep.engine.core.PostDecisionHandling
import com.wingedsheep.engine.event.PendingTrigger
import com.wingedsheep.engine.event.StateTriggerPoller
import com.wingedsheep.engine.event.TriggerDetector
import com.wingedsheep.engine.event.TriggerProcessor
import com.wingedsheep.engine.mechanics.StateBasedActionChecker
import com.wingedsheep.engine.mechanics.combat.CombatDefenders
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.sdk.core.Step
import java.util.UUID

/**
 * Completes one defender's blocker declaration.
 *
 * In Free-for-All multiplayer, declaration events and their triggers are retained until every
 * defender has declared. The final declaration owns the full priority boundary: repeatedly apply
 * SBAs, poll state triggers once after stabilization, place the combined trigger wave, then give
 * the active player priority. Intermediate declarations do none of that work.
 *
 * A shared-turn team is marked as complete by its one atomic declaration, then takes this same
 * final boundary as every other completed block declaration.
 */
internal object BlockDeclarationFinalizer {
    fun finishIfComplete(
        state: GameState,
        triggerDetector: TriggerDetector,
        triggerProcessor: TriggerProcessor,
        sbaChecker: StateBasedActionChecker,
        stateTriggerPoller: StateTriggerPoller,
    ): ExecutionResult? {
        if (state.step != Step.DECLARE_BLOCKERS || CombatDefenders.nextDefenderToDeclare(state) != null) {
            return null
        }
        return finish(
            state,
            emptyList(),
            triggerDetector,
            triggerProcessor,
            sbaChecker,
            stateTriggerPoller,
        )
    }

    fun finish(
        state: GameState,
        events: List<GameEvent>,
        triggerDetector: TriggerDetector,
        triggerProcessor: TriggerProcessor,
        sbaChecker: StateBasedActionChecker,
        stateTriggerPoller: StateTriggerPoller,
    ): ExecutionResult {
        // Capture declaration/payment triggers before any final-boundary SBA can remove a source.
        val newlyDetected = triggerDetector.detectTriggers(state, events)
        val (deferred, stateWithoutDeferred) = removeDeferredBlockTriggers(state)
        val captured = (deferred?.triggers ?: emptyList()) + newlyDetected

        val nextDefender = CombatDefenders.nextDefenderToDeclare(stateWithoutDeferred)
        if (nextDefender != null) {
            val deferredState = if (captured.isEmpty()) {
                stateWithoutDeferred
            } else {
                stateWithoutDeferred.copy(
                    continuationStack = stateWithoutDeferred.continuationStack +
                        DeferredBlockTriggersContinuation(
                            decisionId = "deferred-block-triggers-${UUID.randomUUID()}",
                            triggers = captured,
                        )
                )
            }
            return ExecutionResult.success(deferredState.withPriority(nextDefender), events)
                .copy(
                    triggersAlreadyProcessed = true,
                    postDecisionHandling = PostDecisionHandling.RETURN_AS_IS,
                )
        }

        return stabilizeAndPlaceTriggers(
            state = stateWithoutDeferred,
            capturedTriggers = captured,
            precedingSbaEvents = emptyList(),
            resultEvents = events,
            triggerDetector = triggerDetector,
            triggerProcessor = triggerProcessor,
            sbaChecker = sbaChecker,
            stateTriggerPoller = stateTriggerPoller,
        )
    }

    /** Resume the final boundary after an SBA choice has completed. */
    fun resumeAfterSbaDecision(
        state: GameState,
        continuation: BlockDeclarationSbaBoundaryContinuation,
        precedingSbaEvents: List<GameEvent>,
        triggerDetector: TriggerDetector,
        triggerProcessor: TriggerProcessor,
        sbaChecker: StateBasedActionChecker,
        stateTriggerPoller: StateTriggerPoller,
    ): ExecutionResult = stabilizeAndPlaceTriggers(
        state = state,
        capturedTriggers = continuation.capturedTriggers,
        precedingSbaEvents = precedingSbaEvents,
        resultEvents = precedingSbaEvents,
        triggerDetector = triggerDetector,
        triggerProcessor = triggerProcessor,
        sbaChecker = sbaChecker,
        stateTriggerPoller = stateTriggerPoller,
    )

    /** Resume only after a complete trigger-placement wave (including all placement decisions). */
    fun resumePostPlacementBoundary(
        state: GameState,
        continuation: BlockDeclarationPostPlacementContinuation,
        precedingEvents: List<GameEvent>,
        triggerDetector: TriggerDetector,
        triggerProcessor: TriggerProcessor,
        sbaChecker: StateBasedActionChecker,
        stateTriggerPoller: StateTriggerPoller,
    ): ExecutionResult = stabilizeAfterTriggerPlacement(
        state = state,
        capturedTriggers = continuation.capturedTriggers,
        precedingSbaEvents = if (continuation.precedingEventsAreSba) precedingEvents else emptyList(),
        resultEvents = precedingEvents,
        triggerDetector = triggerDetector,
        triggerProcessor = triggerProcessor,
        sbaChecker = sbaChecker,
        stateTriggerPoller = stateTriggerPoller,
    )

    private fun stabilizeAndPlaceTriggers(
        state: GameState,
        capturedTriggers: List<PendingTrigger>,
        precedingSbaEvents: List<GameEvent>,
        resultEvents: List<GameEvent>,
        triggerDetector: TriggerDetector,
        triggerProcessor: TriggerProcessor,
        sbaChecker: StateBasedActionChecker,
        stateTriggerPoller: StateTriggerPoller,
    ): ExecutionResult {
        // Events produced by a prior SBA choice disappear at the next external action boundary,
        // so capture their triggers before starting the next stabilization pass.
        val triggersBeforePass = capturedTriggers +
            triggerDetector.detectTriggers(state, precedingSbaEvents)
        val baseStackSize = state.continuationStack.size
        val sbaResult = sbaChecker.checkAndApply(state)

        if (sbaResult.isPaused) {
            val capturedThroughPause = triggersBeforePass +
                triggerDetector.detectTriggers(sbaResult.state, sbaResult.events)
            val boundary = BlockDeclarationSbaBoundaryContinuation(
                decisionId = "block-declaration-sba-boundary-${UUID.randomUUID()}",
                capturedTriggers = capturedThroughPause,
            )
            val stack = sbaResult.state.continuationStack
            val pausedState = sbaResult.state.copy(
                continuationStack = stack.subList(0, baseStackSize) + boundary +
                    stack.subList(baseStackSize, stack.size)
            )
            return ExecutionResult.paused(
                pausedState,
                sbaResult.pendingDecision!!,
                resultEvents + sbaResult.events,
            ).copy(
                triggersAlreadyProcessed = true,
                postDecisionHandling = PostDecisionHandling.RETURN_AS_IS,
            )
        }

        val allEvents = resultEvents + sbaResult.events
        if (sbaResult.newState.gameOver) {
            return ExecutionResult.success(sbaResult.newState, allEvents).copy(
                triggersAlreadyProcessed = true,
                postDecisionHandling = PostDecisionHandling.RETURN_AS_IS,
            )
        }

        val sbaTriggers = triggerDetector.detectTriggers(sbaResult.newState, sbaResult.events)
        // State triggers are checked once, only after the complete SBA loop is stable and before
        // the active player would receive priority.
        val pollResult = stateTriggerPoller.poll(sbaResult.newState)
        val allTriggers = triggersBeforePass + sbaTriggers + pollResult.pendingTriggers
        if (allTriggers.isEmpty()) {
            return ExecutionResult.success(
                pollResult.newState.withPriority(pollResult.newState.activePlayerId),
                allEvents,
            ).copy(
                triggersAlreadyProcessed = true,
                postDecisionHandling = PostDecisionHandling.RETURN_AS_IS,
            )
        }

        return placeTriggersThenStabilize(
            state = pollResult.newState,
            triggers = allTriggers,
            resultEvents = allEvents,
            triggerDetector = triggerDetector,
            triggerProcessor = triggerProcessor,
            sbaChecker = sbaChecker,
            stateTriggerPoller = stateTriggerPoller,
        )
    }

    private fun placeTriggersThenStabilize(
        state: GameState,
        triggers: List<PendingTrigger>,
        resultEvents: List<GameEvent>,
        triggerDetector: TriggerDetector,
        triggerProcessor: TriggerProcessor,
        sbaChecker: StateBasedActionChecker,
        stateTriggerPoller: StateTriggerPoller,
    ): ExecutionResult {
        val boundary = BlockDeclarationPostPlacementContinuation(
            decisionId = "block-declaration-post-placement-${UUID.randomUUID()}",
        )
        val stateWithBoundary = state.pushContinuation(boundary)
        val triggerResult = triggerProcessor.processTriggers(stateWithBoundary, triggers)
        if (triggerResult.isPaused) {
            return ExecutionResult.paused(
                triggerResult.state,
                triggerResult.pendingDecision!!,
                resultEvents + triggerResult.events,
            ).copy(
                triggersAlreadyProcessed = true,
                postDecisionHandling = PostDecisionHandling.RETURN_AS_IS,
            )
        }
        if (!triggerResult.isSuccess) {
            val errorState = if (triggerResult.state.peekContinuation() == boundary) {
                triggerResult.state.popContinuation().second
            } else {
                triggerResult.state
            }
            return ExecutionResult(
                state = errorState,
                events = resultEvents + triggerResult.events,
                error = triggerResult.error,
                triggersAlreadyProcessed = true,
                postDecisionHandling = PostDecisionHandling.RETURN_AS_IS,
            )
        }

        val (completedBoundary, stateWithoutBoundary) = triggerResult.newState.popContinuation()
        check(completedBoundary == boundary) {
            "Trigger placement completed without returning to its block-declaration boundary"
        }
        return stabilizeAfterTriggerPlacement(
            state = stateWithoutBoundary,
            capturedTriggers = emptyList(),
            precedingSbaEvents = emptyList(),
            resultEvents = resultEvents + triggerResult.events,
            triggerDetector = triggerDetector,
            triggerProcessor = triggerProcessor,
            sbaChecker = sbaChecker,
            stateTriggerPoller = stateTriggerPoller,
        )
    }

    private fun stabilizeAfterTriggerPlacement(
        state: GameState,
        capturedTriggers: List<PendingTrigger>,
        precedingSbaEvents: List<GameEvent>,
        resultEvents: List<GameEvent>,
        triggerDetector: TriggerDetector,
        triggerProcessor: TriggerProcessor,
        sbaChecker: StateBasedActionChecker,
        stateTriggerPoller: StateTriggerPoller,
    ): ExecutionResult {
        // Placement events are intentionally absent from [precedingSbaEvents]: TriggerProcessor
        // owns their complete CR 603.3b wave. Only fresh SBA events enter this next wave.
        val triggersBeforePass = capturedTriggers +
            triggerDetector.detectTriggers(state, precedingSbaEvents)
        val baseStackSize = state.continuationStack.size
        val sbaResult = sbaChecker.checkAndApply(state)

        if (sbaResult.isPaused) {
            val capturedThroughPause = triggersBeforePass +
                triggerDetector.detectTriggers(sbaResult.state, sbaResult.events)
            val boundary = BlockDeclarationPostPlacementContinuation(
                decisionId = "block-declaration-post-placement-sba-${UUID.randomUUID()}",
                capturedTriggers = capturedThroughPause,
                precedingEventsAreSba = true,
            )
            val stack = sbaResult.state.continuationStack
            val pausedState = sbaResult.state.copy(
                continuationStack = stack.subList(0, baseStackSize) + boundary +
                    stack.subList(baseStackSize, stack.size)
            )
            return ExecutionResult.paused(
                pausedState,
                sbaResult.pendingDecision!!,
                resultEvents + sbaResult.events,
            ).copy(
                triggersAlreadyProcessed = true,
                postDecisionHandling = PostDecisionHandling.RETURN_AS_IS,
            )
        }

        val allEvents = resultEvents + sbaResult.events
        if (sbaResult.newState.gameOver) {
            return ExecutionResult.success(sbaResult.newState, allEvents).copy(
                triggersAlreadyProcessed = true,
                postDecisionHandling = PostDecisionHandling.RETURN_AS_IS,
            )
        }

        val sbaTriggers = triggerDetector.detectTriggers(sbaResult.newState, sbaResult.events)
        val pollResult = stateTriggerPoller.poll(sbaResult.newState)
        val nextTriggers = triggersBeforePass + sbaTriggers + pollResult.pendingTriggers
        if (nextTriggers.isEmpty()) {
            return ExecutionResult.success(
                pollResult.newState.withPriority(pollResult.newState.activePlayerId),
                allEvents,
            ).copy(
                triggersAlreadyProcessed = true,
                postDecisionHandling = PostDecisionHandling.RETURN_AS_IS,
            )
        }

        return placeTriggersThenStabilize(
            state = pollResult.newState,
            triggers = nextTriggers,
            resultEvents = allEvents,
            triggerDetector = triggerDetector,
            triggerProcessor = triggerProcessor,
            sbaChecker = sbaChecker,
            stateTriggerPoller = stateTriggerPoller,
        )
    }

    private fun removeDeferredBlockTriggers(
        state: GameState,
    ): Pair<DeferredBlockTriggersContinuation?, GameState> {
        val deferred = state.peekContinuation() as? DeferredBlockTriggersContinuation
            ?: return null to state
        return deferred to state.popContinuation().second
    }
}
