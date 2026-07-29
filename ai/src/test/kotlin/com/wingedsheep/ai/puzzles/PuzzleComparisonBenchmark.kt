package com.wingedsheep.ai.puzzles

import com.wingedsheep.ai.engine.AiProfile
import com.wingedsheep.ai.engine.EvaluationWeights
import com.wingedsheep.ai.engine.rollout.RolloutSettings
import com.wingedsheep.engine.support.ScenarioTestBase

/**
 * The same 48 puzzles across several agents, side by side.
 *
 * ```
 * just arena-puzzles-compare
 * ```
 *
 * Disabled unless `-Dbenchmark=true`, so a normal `:ai:test` run only pays for [PuzzleSuiteTest].
 * The value over the plain suite is attribution: the `v0` / `production` columns say whether the
 * card advisors are earning anything on the tactics they were written for, and later phases add a
 * column each.
 */
class PuzzleComparisonBenchmark : ScenarioTestBase() {

    init {
        val enabled = System.getProperty("benchmark") == "true"

        test("puzzles: profile comparison").config(enabled = enabled) {
            val runner = PuzzleRunner(cardRegistry) { scenario() }
            val profiles = listOf(
                AiProfile.LEGACY_V0,
                AiProfile.PRODUCTION,
                // Phase 7's rollout evaluator, isolated from Phases 4 and 6 so the column is
                // attributable to the rollouts alone.
                AiProfile.PHASE7,
                // The mixture's own controls: pure rollout at one end, today's static leaf at the
                // other. `staticWeight` is the one parameter Phase 7 could not derive in advance.
                AiProfile.PHASE7.copy(
                    id = "v0-rollout-pure",
                    rollouts = RolloutSettings.DEFAULT.copy(staticWeight = 0.0),
                ),
                AiProfile.PHASE7.copy(
                    id = "v0-rollout-25",
                    rollouts = RolloutSettings.DEFAULT.copy(staticWeight = 0.25),
                ),
                AiProfile.PHASE7.copy(
                    id = "v0-rollout-75",
                    rollouts = RolloutSettings.DEFAULT.copy(staticWeight = 0.75),
                ),
                // Phases 4 and 6 without the rollouts — the reference the full stack has to beat
                // for Phase 7 to be earning anything on top of what already shipped.
                AiProfile.PHASE4_PHASE6,
                // Everything the plan proposes to ship.
                AiProfile.PHASE4_PHASE6_PHASE7,
                // The discrimination control, same as the arena's: every weight zero.
                AiProfile.LEGACY_V0.copy(id = "v0-blind", evaluationWeights = EvaluationWeights.BLIND),
            )

            val runs = profiles.map { profile ->
                val results = runner.runAll(PuzzleCatalog.all, profile)
                println(PuzzleReport.summary(profile.id, results))
                println()
                profile.id to results
            }

            println(PuzzleReport.comparison(runs))
        }
    }
}
