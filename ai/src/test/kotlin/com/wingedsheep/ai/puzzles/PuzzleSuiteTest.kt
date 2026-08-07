package com.wingedsheep.ai.puzzles

import com.wingedsheep.ai.engine.AiProfile
import com.wingedsheep.engine.support.ScenarioTestBase
import io.kotest.assertions.withClue
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
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
 * The reference profile stays [AiProfile.PRODUCTION] even though
 * [AiProfile.PRODUCTION_CANDIDATE_LANDDROP] is what players face since 2026-08-08. `KNOWN_FAILURES`
 * is only meaningful if it describes a *fixed* agent — repointing it at whatever is live would
 * make every promotion rewrite the set it is supposed to be checked against, and the rollout
 * evaluator would also put ~66 playout-driven positions into an always-on suite that runs in
 * seconds today. `PuzzleComparisonBenchmark` is where the live profile is scored, alongside every
 * other one.
 */
class PuzzleSuiteTest : ScenarioTestBase() {

    init {
        val runner = PuzzleRunner(cardRegistry) { scenario() }

        test("every category carries at least six puzzles, with unique ids") {
            PuzzleCatalog.all.map { it.id }.toSet().size shouldBe PuzzleCatalog.all.size
            PuzzleCategory.entries.forEach { category ->
                withClue(category) {
                    PuzzleCatalog.byCategory(category).size shouldBeGreaterThanOrEqual 6
                }
            }
            PuzzleCatalog.all.size shouldBe 78
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
                evalWeightsId = "blind",
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
         * Baselined 2026-07-27 against `AiProfile.PRODUCTION` at 39/48; **44/48 since Phase 6**
         * (`CardIntent`), which closed noncreature-01/03/04 and instants-01/06; **60/66 since Phase
         * 2b** added the respond / activate / keywords categories. Per-category rates are in
         * `docs/ai/baseline-metrics.md`.
         */
        val KNOWN_FAILURES: Set<String> = setOf(
            // A one-ply evaluator cannot see a prevention effect: the state right after Fog
            // resolves has the same life totals as passing, so Fog is only ever "-1 card".
            // Needs the rollout evaluator (Phase 7) to play out the damage step.
            "instants-05",
            // `CardAdvantage.cardValue(0) = -3.0` makes emptying your hand read as a disaster, so
            // the AI holds its last land rather than playing it. sequencing-04 is the same decision
            // with one card of slack and passes.
            //
            // **Closed by `AiProfile.landDropIsNotCardLoss`**, which stops charging the land drop as
            // card loss at all, and still fails here only because [AiProfile.PRODUCTION] is the
            // frozen baseline this set describes. Measured on the 66-puzzle suite: it is the *only*
            // verdict that moves, for `production`, `production-horizon-concave-2` and the live
            // `production-candidate-tuned` alike (+1 each, nothing broken).
            "sequencing-02",
            // No model of "keep a blocker home": every attacker is scored on the damage it deals.
            "race-03",
            // The same `cardValue(0)` cliff as sequencing-02, measured exactly: with one card in
            // hand, casting the Disenchant costs 4.0 of card advantage, and destroying an anthem
            // behind an *empty* board gains 2.4 of board value (weight 1.5 → +3.6). It misses by
            // 0.40. Phase 6 fixed the blindness — the AI now sees the anthem, ranks it correctly
            // and casts at noncreature-01/03/04 — but it cannot outvote a hand-drawn constant that
            // Phase 9 exists to refit. Raising the anthem prior until this passes would be tuning
            // one guess to cancel another.
            "noncreature-02",

            // ── Phase 2b ──
            // A regeneration shield is bought *before* the destruction it answers, so at the moment
            // of the activation the board is unchanged and two mana are gone — the same shape as
            // instants-05's Fog, and the same fix. Phase 7.
            "respond-05",
            // Pumping an unblocked attacker pays now for damage that lands at the combat-damage
            // step. `evaluate1Ply` simulates to the next quiet state, which is still inside
            // declare-blockers, so the +1/+0 shows up as `attackPotential` on a creature that is
            // already attacking and never as life off the opponent. Phase 7.
            "activate-05",
        )
    }
}
