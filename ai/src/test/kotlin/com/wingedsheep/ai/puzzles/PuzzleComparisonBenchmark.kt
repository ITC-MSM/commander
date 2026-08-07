package com.wingedsheep.ai.puzzles

import com.wingedsheep.ai.engine.AiProfile
import com.wingedsheep.ai.engine.rollout.RolloutSettings
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
                // The promotion pair. `production` is what ships today and `production-candidate`
                // is what flipping Phases 4/7/8 on would ship, so this is the only column pair on
                // the suite that prices the decision actually in front of us.
                AiProfile.PRODUCTION_CANDIDATE,
                // The two cheap targeted fixes, with and without the rollouts on top. If
                // `production-tuned` matches `production-candidate-tuned` on the suite, the
                // rollouts are not what is closing these puzzles.
                AiProfile.PRODUCTION_TUNED,
                AiProfile.PRODUCTION_CANDIDATE_TUNED,
                // One variable each, so a puzzle that moves can be attributed.
                AiProfile.PRODUCTION_HORIZON,
                AiProfile.PRODUCTION_CONCAVE,
                AiProfile.PRODUCTION_CONCAVE_2,
                AiProfile.PRODUCTION_HORIZON_CONCAVE,
                AiProfile.PRODUCTION_HORIZON_CONCAVE_2,
                AiProfile.PRODUCTION_CRACKBACK,
                AiProfile.PRODUCTION_TARGETED,
                // The land-drop accounting alone, and on top of what is live. `sequencing-02` is the
                // one verdict that moves in either column, which is what makes it attributable.
                AiProfile.PRODUCTION_LANDDROP,
                AiProfile.PRODUCTION_CANDIDATE_LANDDROP,
                // Land order, alone and on top of what is live. `sequencing-07` is the verdict that
                // should move, with `sequencing-08` — the same board, the opposite answer — held.
                AiProfile.PRODUCTION_LANDSEQ,
                AiProfile.PRODUCTION_CANDIDATE_LANDSEQ,
                // The combat trick window, alone and on top of what is live. `instants-08` is the
                // verdict that should move, with `instants-07` — the same board one step later,
                // the opposite answer — held. Only the candidate column can move `instants-07`:
                // spending the trick needs the budget half, which needs tiers.
                AiProfile.PRODUCTION_TRICKWINDOW,
                AiProfile.PRODUCTION_CANDIDATE_TRICKWINDOW,
                // The race-clock bound, alone and on top of what is live. `lastchance-05` is the
                // verdict that should move. This one touches every position with an empty board,
                // so the column is read for what it *costs* as much as for what it closes.
                AiProfile.PRODUCTION_RACECLOCK,
                AiProfile.PRODUCTION_CANDIDATE_RACECLOCK,
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
                AiProfile.LEGACY_V0.copy(id = "v0-blind", evalWeightsId = "blind"),
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
