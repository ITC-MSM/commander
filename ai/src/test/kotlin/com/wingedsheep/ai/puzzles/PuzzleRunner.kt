package com.wingedsheep.ai.puzzles

import com.wingedsheep.ai.engine.AIPlayer
import com.wingedsheep.ai.engine.AiProfile
import com.wingedsheep.engine.core.ActionProcessor
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.support.ScenarioTestBase

/** How one agent did on one puzzle. [failure] is null exactly when [passed] is true. */
data class PuzzleResult(
    val puzzle: AiPuzzle,
    val passed: Boolean,
    /** [PuzzleMove.describe] of the chosen move, or why no move could be obtained. */
    val move: String,
    val failure: String?,
)

/**
 * Builds a puzzle position, asks an [AiProfile] for one move, and scores it.
 *
 * Takes the registry and scenario factory rather than extending [ScenarioTestBase] itself: the
 * builder is an inner class, so it needs a spec instance to exist, and the runner wants to be
 * callable from more than one spec (the always-on suite and the benchmark-gated report).
 *
 * A fresh [AIPlayer] per puzzle, never a shared one: `GameSimulator.isResolving` and
 * `decisionResolver` are mutable instance state.
 */
class PuzzleRunner(
    private val registry: CardRegistry,
    private val newScenario: () -> ScenarioTestBase.ScenarioBuilder,
) {
    private val processor = ActionProcessor(registry)

    fun run(puzzle: AiPuzzle, profile: AiProfile): PuzzleResult {
        val game = try {
            // `ScenarioBuilder` seeds itself from `System.nanoTime()` so repeated builds vary coin
            // flips. A CI gate cannot be seeded from the clock — pin it, exactly as the arena pins
            // `GameConfig.seed`.
            puzzle.position(newScenario().withRngSeed(PUZZLE_SEED))
        } catch (e: Throwable) {
            return PuzzleResult(puzzle, false, "—", "position failed to build: ${e.message}")
        }

        val state = game.state
        val aiId = game.seatId(puzzle.aiSeat)
        val opponentId = game.seatId(if (puzzle.aiSeat == 1) 2 else 1)

        // A position where the AI is not the one to move scores whatever the first branch of the
        // check happens to say — which is worse than no puzzle at all. Fail loudly instead.
        state.pendingDecision?.let {
            return PuzzleResult(puzzle, false, "—", "position paused on ${it::class.simpleName}")
        }
        if (state.priorityPlayerId != aiId) {
            return PuzzleResult(
                puzzle, false, "—",
                "position gives priority to ${state.priorityPlayerId} at ${state.phase}/${state.step}, " +
                    "not the AI (seat ${puzzle.aiSeat})",
            )
        }

        val ai = AIPlayer.create(registry, aiId, profile)
        val action = try {
            ai.chooseAction(state)
        } catch (e: Throwable) {
            return PuzzleResult(puzzle, false, "—", "AI threw ${e::class.simpleName}: ${e.message}")
        }
        val move = PuzzleMove(state, aiId, opponentId, action)

        // The arena found the AI proposing ~0.9 illegal actions per game. A puzzle that "passes"
        // on an action the engine would reject is measuring nothing, so legality is part of the bar.
        processor.process(state, action).result.error?.let { error ->
            return PuzzleResult(puzzle, false, move.describe(), "engine rejected the move: $error")
        }

        return try {
            puzzle.check(move)
            PuzzleResult(puzzle, true, move.describe(), null)
        } catch (e: AssertionError) {
            PuzzleResult(puzzle, false, move.describe(), e.message ?: "assertion failed")
        }
    }

    fun runAll(puzzles: List<AiPuzzle>, profile: AiProfile): List<PuzzleResult> =
        puzzles.map { run(it, profile) }

    private companion object {
        /** Same date-stamp convention as the arena's `ArenaConfig.DEFAULT_SEED`. */
        const val PUZZLE_SEED = 20260727L
    }
}
