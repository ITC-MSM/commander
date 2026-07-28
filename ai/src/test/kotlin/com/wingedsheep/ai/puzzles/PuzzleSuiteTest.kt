package com.wingedsheep.ai.puzzles

import com.wingedsheep.ai.engine.AiProfile
import com.wingedsheep.ai.engine.EvaluationWeights
import com.wingedsheep.engine.support.ScenarioTestBase
import io.kotest.assertions.withClue
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe

/**
 * The always-on tactical suite. Runs in seconds; `just arena-puzzles`.
 *
 * The gate is **"the failing set equals [KNOWN_FAILURES]"**, not "everything passes". Today's AI
 * solves some of these and not others, and a suite pinned to 48/48 would be red forever and
 * therefore ignored. Equality flags a regression *and* an unexpected fix — if a change makes the
 * AI solve `noncreature-04`, this test fails until the id is deleted from the set, which is
 * exactly the moment you want to notice.
 *
 * The reference profile is [AiProfile.PRODUCTION] — what a player actually faces, card advisors
 * included. `PuzzleComparisonBenchmark` runs the same suite across profiles.
 */
class PuzzleSuiteTest : ScenarioTestBase() {

    init {
        val runner = PuzzleRunner(cardRegistry) { scenario() }

        test("the suite covers eight categories, six puzzles each, with unique ids") {
            PuzzleCatalog.all.map { it.id }.toSet().size shouldBe PuzzleCatalog.all.size
            PuzzleCategory.entries.forEach { category ->
                withClue(category) { PuzzleCatalog.byCategory(category).size shouldBe 6 }
            }
            PuzzleCatalog.all.size shouldBe 48
        }

        test("every KNOWN_FAILURES id names a real puzzle") {
            (KNOWN_FAILURES - PuzzleCatalog.all.map { it.id }.toSet()) shouldBe emptySet()
        }

        test("the AI solves every puzzle outside KNOWN_FAILURES") {
            val results = runner.runAll(PuzzleCatalog.all, AiProfile.PRODUCTION)
            println(PuzzleReport.summary(AiProfile.PRODUCTION.id, results))
            results.filterNot { it.passed }.map { it.puzzle.id }.toSet() shouldBe KNOWN_FAILURES
        }

        // The arena proved it discriminates by beating a zero-weight agent 200-0. Same argument,
        // same control: if an evaluator that scores every board identically solves as many
        // positions as the real one, these are not puzzles, they are coin flips.
        test("a zero-weight agent solves strictly fewer puzzles") {
            val blind = AiProfile.LEGACY_V0.copy(
                id = "v0-blind",
                evaluationWeights = EvaluationWeights.BLIND,
            )
            val blindResults = runner.runAll(PuzzleCatalog.all, blind)
            val blindPassed = blindResults.count { it.passed }
            println(PuzzleReport.summary(blind.id, blindResults))
            blindPassed shouldBeLessThan (PuzzleCatalog.all.size - KNOWN_FAILURES.size)
        }
    }

    companion object {
        /**
         * Puzzles today's AI does not solve. **Shrinking this set is the deliverable of Phases 3–9**
         * of `backlog/engine-ai-improvement.md`; growing it needs a reason in the commit message.
         *
         * Baselined 2026-07-27 against `AiProfile.PRODUCTION`. Per-category rates are in
         * `docs/ai/baseline-metrics.md`.
         */
        val KNOWN_FAILURES: Set<String> = setOf(
            // Instant timing is ad-hoc: `Strategist` has no notion of "this spell wants a window",
            // only a hard-coded `passScore - 1.5` on the opponent's end step. Phase 6's `HoldPolicy`.
            "instants-01",
            "instants-06",
            // A one-ply evaluator cannot see a prevention effect: the state right after Fog
            // resolves has the same life totals as passing, so Fog is only ever "-1 card".
            // Needs the rollout evaluator (Phase 7) to play out the damage step.
            "instants-05",
            // `CardAdvantage.cardValue(0) = -3.0` makes emptying your hand read as a disaster, so
            // the AI holds its last land rather than playing it. Phase 9 refits these constants;
            // sequencing-04 is the same decision with one card of slack and passes.
            "sequencing-02",
            // No model of "keep a blocker home": every attacker is scored on the damage it deals.
            "race-03",
            // The headline blindness. `BoardFeatures.permanentValue` flat-values every non-creature
            // permanent at 0.5 and `heuristicTargetRank` ranks one at 0.0, so destroying an artifact
            // gains +0.5 board and costs -1 card — the AI would rather hold the removal forever.
            // Phase 6 (`CardIntent`) exists to fix exactly this; noncreature-05/06 already pass
            // because their effect shows up in *creature* stats, which the evaluator can see.
            "noncreature-01",
            "noncreature-02",
            "noncreature-03",
            "noncreature-04",
        )
    }
}
