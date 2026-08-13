package com.wingedsheep.engine.handlers.continuations

import com.wingedsheep.engine.core.CastSpellPostAdditionalCostsContinuation
import com.wingedsheep.engine.core.EngineServices
import com.wingedsheep.engine.core.ExecutionResult
import com.wingedsheep.engine.core.GameEvent
import com.wingedsheep.engine.handlers.actions.spell.CastSpellHandler
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.sdk.core.Zone

/**
 * Completes a spell's normal ReturnToHand additional cost after a replacement choice.
 *
 * It is an auto-resumer: the player has already made the only decision.  For a multi-return
 * atom it attempts the next selected permanent in declaration order, inserting the same frame
 * beneath any new replacement continuation.  Only after every physical move has happened does
 * it enter CastSpellHandler's isolated post-additional-cost tail.
 */
class CastSpellPostAdditionalCostsContinuationResumer(
    services: EngineServices
) : AutoResumerModule {
    private val castSpellHandler: CastSpellHandler by lazy { CastSpellHandler.create(services) }

    override fun autoResumers(): List<AutoResumer<*>> = listOf(
        autoResumer(CastSpellPostAdditionalCostsContinuation::class) { state, continuation, events, checkForMore ->
            resume(state, continuation, events, checkForMore)
        }
    )

    private fun resume(
        state: GameState,
        continuation: CastSpellPostAdditionalCostsContinuation,
        physicalMoveEvents: List<GameEvent>,
        @Suppress("UNUSED_PARAMETER") checkForMore: CheckForMore
    ): ExecutionResult {
        val next = continuation.remainingPermanentIds.firstOrNull()
            ?: return castSpellHandler.resumePostAdditionalCosts(state, continuation, physicalMoveEvents)

        val attempt = ZoneTransitionService.attemptMoveToZone(state, next, Zone.HAND)
        val allEvents = physicalMoveEvents + attempt.events
        val rest = continuation.copy(remainingPermanentIds = continuation.remainingPermanentIds.drop(1))
        if (!attempt.isPaused) return resume(attempt.state, rest, allEvents, checkForMore)

        val stack = attempt.state.continuationStack
        if (stack.isEmpty()) return ExecutionResult.error(attempt.state, "Zone replacement paused without a continuation")
        val pausedState = attempt.state.copy(
            continuationStack = stack.dropLast(1) + rest + stack.last()
        )
        return ExecutionResult.paused(pausedState, attempt.pendingDecision!!, allEvents)
    }
}
