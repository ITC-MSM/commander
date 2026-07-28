package com.wingedsheep.ai.engine.evaluation

import com.wingedsheep.engine.mechanics.layers.ProjectedState
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.sdk.model.EntityId

/**
 * Evaluates how favorable a game state is for a given player.
 *
 * Returns a score where higher is better. Positive means ahead, negative means behind.
 * [Double.MAX_VALUE] means the player has won; [Double.MIN_VALUE] means they've lost.
 */
fun interface BoardEvaluator {
    fun evaluate(state: GameState, projected: ProjectedState, playerId: EntityId): Double
}

/**
 * Combines multiple [BoardFeature]s with weights into a single evaluator.
 */
class CompositeBoardEvaluator(
    private val features: List<Pair<Double, BoardFeature>>
) : BoardEvaluator {

    override fun evaluate(state: GameState, projected: ProjectedState, playerId: EntityId): Double {
        // Terminal states. `winnerId` records one *representative* of the winning team
        // (GameEndCheck), so a Two-Headed Giant teammate who is not that representative has still
        // won — comparing it to playerId directly would score half of every won team game as a loss.
        if (state.gameOver) {
            return when {
                state.winnerId == null -> 0.0 // draw
                state.winnerId in state.teamOf(playerId) -> Double.MAX_VALUE / 2
                else -> -(Double.MAX_VALUE / 2)
            }
        }

        // CR 104.3b — in a pod a player is eliminated while the game carries on, so "I lost" and
        // "the game is over" are different questions. Without this a rollout that eliminates us
        // scores off the surviving opponents' boards, which can read as *better* than surviving.
        // A team is only out once every member is (CR 104.2c).
        if (state.teamActivePlayers(playerId).isEmpty()) return -(Double.MAX_VALUE / 2)

        return features.sumOf { (weight, feature) -> weight * feature.score(state, projected, playerId) }
    }
}

/**
 * A single scoring dimension. Returns a raw (unnormalized) score from the AI player's perspective.
 */
fun interface BoardFeature {
    fun score(state: GameState, projected: ProjectedState, playerId: EntityId): Double
}
