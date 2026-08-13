package com.wingedsheep.engine.handlers.continuations

import com.wingedsheep.engine.core.*
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.replacement.*
import com.wingedsheep.engine.state.GameState

/**
 * Continuation resumers for the replacement effect system.
 *
 * Handles:
 * - [ReplacementChoiceContinuation] — player chose between competing replacements
 *   (decision-driven resumer)
 * - [ReplacementResolveContinuation] — after a replacement chain completes,
 *   resume the original context (auto-resumer)
 */
class ReplacementContinuationResumer(
    private val processor: ReplacementEffectProcessor,
    private val services: EngineServices
) : ContinuationResumerModule, AutoResumerModule {

    override fun resumers(): List<ContinuationResumer<*>> = listOf(
        resumer(ReplacementChoiceContinuation::class, ::resumeReplacementChoice),
        resumer(OptionalReplacementContinuation::class, ::resumeOptionalReplacement)
    )

    private fun resumeOptionalReplacement(
        state: GameState,
        continuation: OptionalReplacementContinuation,
        response: DecisionResponse,
        checkForMore: CheckForMore
    ): ExecutionResult {
        if (response !is YesNoResponse) {
            return ExecutionResult.error(state, "Expected yes/no response for optional replacement")
        }
        val applied = continuation.alreadyApplied + continuation.replacement.identity
        val result = if (response.choice) {
            processor.applySingle(state, continuation.replacement, continuation.pendingEvent, continuation.alreadyApplied)
        } else {
            processor.process(
                state.copy(activeReplacementChain = applied), continuation.pendingEvent,
                continuation.context, applied
            )
        }
        return finishPendingEvent(state, continuation.pendingEvent, continuation.context, result, checkForMore)
    }

    override fun autoResumers(): List<AutoResumer<*>> = listOf(
        autoResumer(ReplacementResolveContinuation::class) { state, continuation, events, checkForMore ->
            resumeReplacementResolve(state, continuation, events, checkForMore)
        },
        autoResumer(ZoneChangePerformContinuation::class) { state, continuation, events, checkForMore ->
            val result = com.wingedsheep.engine.handlers.effects.ZoneTransitionService.moveToZone(
                state, continuation.entityId, continuation.destination,
                continuation.options.copy(
                    skipZoneChangeRedirect = true,
                    linkExileToSourceId = continuation.linkExileToSourceId
                ), continuation.fromZoneKey
            )
            val rider = continuation.postMoveEffect
            val transitionEvents = if (continuation.options.suppressZoneChangeEvent) emptyList() else result.events
            if (rider == null) {
                checkForMore(result.state, events + transitionEvents)
            } else {
                val context = continuation.postMoveContext
                    ?: EffectContext(null, continuation.entityId)
                val riderResult = services.effectExecutorRegistry.execute(result.state, rider, context)
                if (riderResult.isPaused) riderResult.toExecutionResult()
                else if (riderResult.error != null) riderResult.toExecutionResult()
                else checkForMore(riderResult.state, events + transitionEvents + riderResult.events)
            }
        },
        autoResumer(DeferredStackZoneMoveContinuation::class) { state, continuation, events, checkForMore ->
            val attempt = com.wingedsheep.engine.handlers.effects.ZoneTransitionService.attemptMoveToZone(
                state = state,
                entityId = continuation.entityId,
                destinationZone = continuation.destination,
                options = continuation.options,
                fromZoneKey = com.wingedsheep.engine.state.ZoneKey(
                    continuation.ownerId, com.wingedsheep.sdk.core.Zone.STACK
                )
            )
            if (attempt.isPaused) {
                ExecutionResult.paused(attempt.state, attempt.pendingDecision!!, events + attempt.events)
            } else {
                checkForMore(attempt.state, events + attempt.events)
            }
        }
    )

    /**
     * Resume after the player chose one of multiple competing replacement
     * effects (CR 616.1e).
     *
     * Delegates outcome computation to [ReplacementEffectProcessor.applySingle],
     * then manages lifecycle (NextUse shield consumption) before resuming
     * the original context.
     */
    private fun resumeReplacementChoice(
        state: GameState,
        continuation: ReplacementChoiceContinuation,
        response: DecisionResponse,
        checkForMore: CheckForMore
    ): ExecutionResult {
        if (response !is OptionChosenResponse) {
            return ExecutionResult.error(state, "Expected option choice response for replacement")
        }

        val chosenIndex = response.optionIndex
        if (chosenIndex < 0 || chosenIndex >= continuation.options.size) {
            return ExecutionResult.error(state, "Invalid replacement choice index: $chosenIndex")
        }

        val chosen = continuation.options[chosenIndex]

        // The processor's applySingle() builds the execution context from floating-shield
        // data when applicable, returning it in ProcessorResult.Resolved.executionContext.
        // Pass continuation.context for condition evaluation during recursive processing.
        val context = continuation.context

        // Push domain-specific remainder continuation (e.g. remaining draws
        // in the draw loop) before the replacement resolves, so it sits below
        // any ReplacementResolveContinuation in the stack and can resume after
        // the replacement effect completes.
        val stateWithRemaining = continuation.pendingEvent.remainderContinuation(state)
            ?.let { state.pushContinuation(it) }
            ?: state

        // Selecting the Commander option from a CR 616 ordering prompt only
        // selects its place in the chain. CR 903.9b still gives the commander
        // owner the separate choice to apply that optional replacement.
        if (chosen.effect is com.wingedsheep.sdk.scripting.CommanderZoneReplacement) {
            val optionalResult = processor.presentOptionalReplacement(
                stateWithRemaining,
                continuation.pendingEvent,
                chosen,
                continuation.alreadyApplied,
                context
            )
            return when (optionalResult) {
                is ProcessorResult.Paused -> ExecutionResult.paused(optionalResult.state, optionalResult.decision)
                else -> error("Commander replacement must present its owner with an optional-replacement decision")
            }
        }

        // Compute the outcome.
        val result = processor.applySingle(
            state = stateWithRemaining,
            gathered = chosen,
            event = continuation.pendingEvent,
            alreadyApplied = continuation.alreadyApplied
        )

        return when (result) {
            is ProcessorResult.Resolved -> {
                // Consume NextUse floating-effect shield if applicable (caller's lifecycle responsibility).
                val stateAfterLifecycle = if (result.identity is ReplacementEffectIdentity.FloatingIdentity) {
                    processor.consumeFloatingEffect(result.state, result.identity.floatingId)
                } else {
                    result.state
                }
                when (val outcome = result.outcome) {
                    is ReplacementOutcome.Replaced -> {
                        val execCtx = result.executionContext ?: context
                        handleReplacedOutcome(stateAfterLifecycle, outcome, execCtx, checkForMore)
                    }
                    is ReplacementOutcome.Consumed -> checkForMore(stateAfterLifecycle, emptyList())
                    is ReplacementOutcome.Modified -> {
                        // Unlike Replaced/Consumed — where the replacement *is* what happens —
                        // a Modified outcome leaves the (modified) event still to be performed.
                        // The call site that would have performed it returned when this paused,
                        // so the event supplies a frame that performs it on resume. Without
                        // this the whole instruction is silently dropped.
                        val performFrame = outcome.modifiedEvent.performContinuation(stateAfterLifecycle)
                        // CR 614.5 is per-event: this event is done being replaced, so the
                        // chain must not leak into the events performing it carries.
                        val cleared = stateAfterLifecycle.copy(activeReplacementChain = null)
                        val stateToResume = performFrame?.let { cleared.pushContinuation(it) } ?: cleared
                        checkForMore(stateToResume, emptyList())
                    }
                }
            }
            is ProcessorResult.Paused -> {
                ExecutionResult.paused(result.state, result.decision)
            }
            is ProcessorResult.Pass -> {
                // Shouldn't happen — the chosen effect was matched
                error("resumeReplacementChoice returned a Pass result")
            }
        }
    }

    /** Complete a generic pending-event chain after a decision without dropping a modified event. */
    private fun finishPendingEvent(
        originalState: GameState,
        pendingEvent: PendingGameEvent,
        context: EffectContext?,
        result: ProcessorResult,
        checkForMore: CheckForMore
    ): ExecutionResult = when (result) {
        is ProcessorResult.Paused -> ExecutionResult.paused(result.state, result.decision)
        is ProcessorResult.Pass -> {
            // This event has settled without another replacement. Its chain is
            // per-event (CR 614.5), so never leak identities into the physical
            // move or the caller's next event.
            val cleared = originalState.copy(activeReplacementChain = null)
            val frame = pendingEvent.performContinuation(cleared)
            checkForMore(frame?.let { cleared.pushContinuation(it) } ?: cleared, emptyList())
        }
        is ProcessorResult.Resolved -> when (val outcome = result.outcome) {
            is ReplacementOutcome.Modified -> {
                val cleared = result.state.copy(activeReplacementChain = null)
                val frame = outcome.modifiedEvent.performContinuation(cleared)
                checkForMore(frame?.let { cleared.pushContinuation(it) } ?: cleared, emptyList())
            }
            is ReplacementOutcome.Consumed -> checkForMore(result.state.copy(activeReplacementChain = null), emptyList())
            is ReplacementOutcome.Replaced -> handleReplacedOutcome(
                result.state.copy(activeReplacementChain = null), outcome, result.executionContext ?: context, checkForMore
            )
        }
    }

    /**
     * Auto-resume after a replacement chain has fully resolved. Pops the
     * [ReplacementResolveContinuation] and calls checkForMore so the original
     * execution context resumes.
     */
    private fun resumeReplacementResolve(
        state: GameState,
        continuation: ReplacementResolveContinuation,
        events: List<GameEvent>,
        checkForMore: CheckForMore
    ): ExecutionResult {
        // The new effect has completed executing. Resume the original context
        // by calling checkForMore.
        return checkForMore(state, events)
    }

    /**
     * Execute the replacement effect for a [ReplacementOutcome.Replaced],
     * then push a [ReplacementResolveContinuation] so the original context
     * resumes after the new effect completes.
     */
    private fun handleReplacedOutcome(
        state: GameState,
        outcome: ReplacementOutcome.Replaced,
        context: EffectContext?,
        checkForMore: CheckForMore
    ): ExecutionResult {
        val zoneEvent = outcome.replacementEvent as? PendingGameEvent.ZoneChangePending
        if (zoneEvent != null) {
            val linkSource = if (zoneEvent.linkExileToSource) context?.sourceId else null
            val perform = ZoneChangePerformContinuation(
                entityId = zoneEvent.entityId,
                destination = zoneEvent.destination,
                options = zoneEvent.options,
                fromZoneKey = zoneEvent.fromZoneKey,
                postMoveEffect = outcome.newEffect,
                postMoveContext = context,
                linkExileToSourceId = linkSource
            )
            return checkForMore(state.copy(activeReplacementChain = null).pushContinuation(perform), emptyList())
        }
        val resumeContinuation = ReplacementResolveContinuation(
            decisionId = "pending"
        )

        val stateWithResumeFrame = state.pushContinuation(resumeContinuation)

        // Execute the new effect
        if (context != null) {
            // The processor stamped activeReplacementChain onto stateWithResumeFrame
            // with all effects applied in this chain, so nested effect execution
            // won't re-trigger them. Clear the chain after execution so the
            // ReplacementResolveContinuation and any remaining draws resume fresh.
            val effectResult = services.effectExecutorRegistry.execute(stateWithResumeFrame, outcome.newEffect, context)
            if (effectResult.isPaused) {
                // Clear chain on pause so subsequent execution is unaffected.
                val clearedState = effectResult.state.copy(activeReplacementChain = null)
                return ExecutionResult(clearedState, effectResult.events, effectResult.error, effectResult.pendingDecision, effectResult.triggersAlreadyProcessed)
            }
            val clearedState = effectResult.state.copy(activeReplacementChain = null)
            return checkForMore(clearedState, effectResult.events)
        }

        return checkForMore(stateWithResumeFrame, emptyList())
    }
}
