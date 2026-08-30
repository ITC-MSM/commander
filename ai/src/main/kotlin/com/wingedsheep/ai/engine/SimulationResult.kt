package com.wingedsheep.ai.engine

import com.wingedsheep.engine.core.GameEvent
import com.wingedsheep.engine.core.PendingDecision
import com.wingedsheep.engine.state.GameState

/**
 * Result of simulating an action through the engine.
 */
sealed interface SimulationResult {
    val state: GameState
    val events: List<GameEvent>

    /**
     * The action reached the simulator's successful quiet/completed stopping boundary.
     * This is simulation-terminal, not necessarily game-terminal; inspect [GameState.gameOver].
     */
    data class Terminal(
        override val state: GameState,
        override val events: List<GameEvent>
    ) : SimulationResult

    /** The action paused mid-resolution — a decision is required. */
    data class NeedsDecision(
        override val state: GameState,
        val decision: PendingDecision,
        override val events: List<GameEvent>
    ) : SimulationResult

    /** The action was illegal or failed validation. */
    data class Illegal(
        override val state: GameState,
        override val events: List<GameEvent>,
        val reason: String
    ) : SimulationResult

    /**
     * Automatic resolution reached its bounded progress guard while another automatic transition
     * remained. The retained state is unfinished and must not be scored as a completed candidate.
     *
     * Two consumers, two answers, and the difference is whether the caller is producing a *move* or
     * a *record*:
     *
     * - **Live play discards.** The AI is a heuristic that ranks candidates; the honest answer to
     *   "this one never finished resolving" is [scoreOrRankLast] or an outright drop, exactly as
     *   [Illegal] is dropped. See [AutomaticResolutionLimitException] for why it must not throw.
     * - **Offline capture refuses.** A training record or a gym transition that quietly stored an
     *   unfinished state would be indistinguishable from a real one forever after, so those call
     *   [requireNoAutomaticResolutionStop] and fail where the fault is.
     */
    data class StoppedAtLimit(
        override val state: GameState,
        override val events: List<GameEvent>,
        val automaticTransitions: Int,
        val limit: Int,
    ) : SimulationResult
}

/**
 * Raised when an *offline* consumer — one whose output is a record rather than a move — is handed an
 * unfinished automatic-resolution state.
 *
 * **Never throw this on a live-play path.** `EngineAiPlayerController` does not catch it and
 * `AiWebSocketSession` swallows it into a log line, so the AI simply never submits a response. That
 * is a wedged game no backstop can see: `GameStallGuard` counts actions that were *applied* and
 * actions that were *rejected*, and a response that is never submitted moves neither counter, so
 * the session is never swept and a tournament round blocks on a match that will never finish. A
 * candidate that ran out of automatic transitions is a candidate to rank last, not a reason to
 * abandon the decision — see [scoreOrRankLast].
 */
class AutomaticResolutionLimitException(
    val stopped: SimulationResult.StoppedAtLimit,
    context: String,
) : IllegalStateException(
    "$context stopped after ${stopped.automaticTransitions}/${stopped.limit} automatic transitions; " +
        "the retained simulation state is unfinished",
)

/**
 * Refuse the one result whose state is diagnostic rather than suitable for evaluation.
 *
 * For offline capture and replay only. Live play uses [scoreOrRankLast] or drops the candidate.
 */
fun SimulationResult.requireNoAutomaticResolutionStop(context: String): SimulationResult {
    if (this is SimulationResult.StoppedAtLimit) {
        throw AutomaticResolutionLimitException(this, context)
    }
    return this
}

/**
 * Score this leaf, or rank it below every candidate that reached a real boundary.
 *
 * The comparison is what the AI actually needs: every caller is a `maxByOrNull` or a `>=` between
 * candidates, so an unfinished simulation has to lose that comparison — not remove it. Dropping the
 * candidate outright is the other valid answer and is what a caller returning `null` already does;
 * this is for the callers that must return *something*.
 */
inline fun SimulationResult.scoreOrRankLast(score: (GameState) -> Double): Double =
    if (this is SimulationResult.StoppedAtLimit) Double.NEGATIVE_INFINITY else score(state)
