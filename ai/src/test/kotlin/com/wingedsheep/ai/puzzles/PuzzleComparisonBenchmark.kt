package com.wingedsheep.ai.puzzles

import com.wingedsheep.ai.engine.AiProfile
import com.wingedsheep.ai.engine.EvaluationWeights
import com.wingedsheep.engine.support.ScenarioTestBase

/**
 * The same 66 puzzles across several agents, side by side.
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
