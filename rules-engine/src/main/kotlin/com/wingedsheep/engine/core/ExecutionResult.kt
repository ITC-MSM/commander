package com.wingedsheep.engine.core

import com.wingedsheep.engine.state.GameState
import kotlinx.serialization.Serializable

/**
 * Result of executing a game action or engine step: the state it left, the events it emitted, and
 * its [outcome] — [Outcome.Done], [Outcome.Paused] on a player's question, or [Outcome.Rejected]
 * with a typed [Rejection].
 *
 * Nested engine steps also use `ExecutionResult` while composing an action and may have built
 * intermediate immutable states before reporting a rejection. [ActionProcessor] is the public
 * transaction boundary: a rejected action exposes the exact input state and retains only the
 * rejection, with no events or pending decision from the attempt.
 *
 * Game-over is signaled via `state.gameOver` + a [GameEndedEvent] in `events`.
 */
@Serializable
data class ExecutionResult(
    val state: GameState,
    val events: List<GameEvent> = emptyList(),
    val outcome: Outcome = Outcome.Done,
) {
    /** The rejection's message, or null when the work was not rejected. */
    val error: String? get() = (outcome as? Outcome.Rejected)?.reason?.message

    /** The question the work stopped on, or null when it did not pause. */
    val pendingDecision: PendingDecision? get() = (outcome as? Outcome.Paused)?.decision

    /** Alias for state to indicate we're getting the resulting state after execution */
    val newState: GameState get() = state

    companion object {
        /**
         * Create a successful result with no events.
         */
        fun success(state: GameState): ExecutionResult =
            ExecutionResult(state)

        /**
         * Create a successful result with events.
         */
        fun success(state: GameState, events: List<GameEvent>): ExecutionResult =
            ExecutionResult(state, events)

        /**
         * Reject work that validation accepted but execution could not carry out. See
         * [Rejection.ExecutionFailed]; [ActionProcessor] reports validation refusals as
         * [Rejection.IllegalAction] itself.
         */
        fun error(state: GameState, message: String): ExecutionResult =
            rejected(state, Rejection.ExecutionFailed(message))

        fun rejected(state: GameState, reason: Rejection, events: List<GameEvent> = emptyList()): ExecutionResult =
            ExecutionResult(state, events, Outcome.Rejected(reason))

        /**
         * Propagate an existing suspension through an outer execution layer. Never allocates or
         * installs a question; the state supplies the single authoritative pending decision.
         */
        fun propagatePause(state: GameState, events: List<GameEvent> = emptyList()): ExecutionResult =
            ExecutionResult(state, events, Outcome.Paused(requireNotNull(state.pendingDecision) {
                "A paused result must propagate an installed suspension"
            }))
    }
}
