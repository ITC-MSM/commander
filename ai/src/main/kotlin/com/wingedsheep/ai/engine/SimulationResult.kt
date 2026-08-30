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
     */
    data class StoppedAtLimit(
        override val state: GameState,
        override val events: List<GameEvent>,
        val automaticTransitions: Int,
        val limit: Int,
    ) : SimulationResult {
        init {
            require(limit > 0) { "limit must be positive" }
            require(automaticTransitions >= limit) {
                "automaticTransitions must have reached limit"
            }
        }
    }
}

/** Raised when a consumer attempts to use an unfinished automatic-resolution state as an outcome. */
class AutomaticResolutionLimitException(
    val stopped: SimulationResult.StoppedAtLimit,
    context: String,
) : IllegalStateException(
    "$context stopped after ${stopped.automaticTransitions}/${stopped.limit} automatic transitions; " +
        "the retained simulation state is unfinished",
)

/** Refuse the one result whose state is diagnostic rather than suitable for evaluation. */
fun SimulationResult.requireNoAutomaticResolutionStop(context: String): SimulationResult {
    if (this is SimulationResult.StoppedAtLimit) {
        throw AutomaticResolutionLimitException(this, context)
    }
    return this
}
