package com.wingedsheep.engine.core

import com.wingedsheep.engine.state.GameState
import kotlinx.serialization.Serializable

/**
 * Result of executing a game action or engine step.
 *
 * The engine operates as a reentrant state machine. Every operation returns one of:
 * - **Success**: error == null && pendingDecision == null
 * - **PausedForDecision**: pendingDecision != null (needs player input)
 * - **Error**: error != null (action was invalid, state unchanged)
 *
 * Game-over is signaled via `state.gameOver` + a [GameEndedEvent] in `events`.
 */
@Serializable
data class ExecutionResult(
    val state: GameState,
    val events: List<GameEvent> = emptyList(),
    val error: String? = null,
    val pendingDecision: PendingDecision? = null,
    /**
     * `true` when the producing action handler already ran [TriggerDetector] over
     * [events] and put any resulting triggers on the stack itself. Callers resuming
     * a paused action (notably `SubmitDecisionHandler`) must skip detection on
     * [events] when this is set; otherwise battlefield triggers like Riku of Many
     * Paths would be duplicated on the stack — once by the handler, once by the
     * resumer running on the same `SpellCastEvent`.
     */
    val triggersAlreadyProcessed: Boolean = false,
    /**
     * Directs [SubmitDecisionHandler] how to treat a successful continuation result.
     *
     * Most decision resumptions have reached an ordinary "a player would receive priority"
     * boundary, so that handler performs its shared SBA/trigger/priority sequence. A small
     * number of turn-based-action continuations instead resume *inside* an engine-owned
     * sequence. In that case the continuation itself owns the exact rules boundary and this
     * value prevents the generic decision path from running a second SBA/trigger pass or
     * replacing the priority cursor it deliberately selected.
     */
    val postDecisionHandling: PostDecisionHandling = PostDecisionHandling.STANDARD
) {
    val isSuccess: Boolean get() = error == null && pendingDecision == null
    val isPaused: Boolean get() = pendingDecision != null

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
         * Create an error result.
         */
        fun error(state: GameState, message: String): ExecutionResult =
            ExecutionResult(state, error = message)

        /**
         * Create a paused result awaiting player input.
         */
        fun paused(state: GameState, decision: PendingDecision, events: List<GameEvent> = emptyList()): ExecutionResult =
            ExecutionResult(state, events, pendingDecision = decision)
    }
}

@Serializable
enum class PostDecisionHandling {
    STANDARD,
    RETURN_AS_IS,
}
