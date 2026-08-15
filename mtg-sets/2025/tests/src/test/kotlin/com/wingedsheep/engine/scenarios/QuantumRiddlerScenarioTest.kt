package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Quantum Riddler (EOE) — {3}{U}{U} Creature — Sphinx, 4/6.
 *
 * "Flying
 *  When this creature enters, draw a card.
 *  As long as you have one or fewer cards in hand, if you would draw one or more cards,
 *  you draw that many cards plus one instead.
 *  Warp {1}{U}"
 *
 * Exercises the new [com.wingedsheep.sdk.scripting.ModifyDrawAmount] replacement:
 * the +1 modifier applies to draw events while the hand-size restriction holds and
 * does not apply once the restriction lifts.
 *
 * Verified by casting Quantum Riddler itself — once it resolves and its ETB
 * "draw a card" trigger goes on the stack, the modifier evaluates the hand size at
 * trigger-resolution time (after Riddler has left hand). With an otherwise empty hand,
 * the ETB-draw 1 becomes draw 2.
 */
class QuantumRiddlerScenarioTest : ScenarioTestBase() {

    init {
        context("Quantum Riddler's conditional +1 draw replacement") {

            test("ETB-draw 1 becomes draw 2 when hand is otherwise empty after casting") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Quantum Riddler")
                    .withLandsOnBattlefield(1, "Island", 5)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Hill Giant")
                    .withCardInLibrary(1, "Mountain")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Quantum Riddler")
                withClue("Cast should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                // After Riddler enters and its ETB-draw trigger resolves:
                // - Hand = empty just before the trigger resolves (Riddler left as cast)
                // - The +1 modifier applies because hand-size ≤ 1, so draw 1 → draw 2.
                withClue("Hand should hold 2 drawn cards (1 + 1 from modifier)") {
                    game.handSize(1) shouldBe 2
                }
            }

            test("ETB-draw 1 stays 1 when hand size > 1 after casting") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Quantum Riddler")
                    .withCardInHand(1, "Plains")
                    .withCardInHand(1, "Swamp")
                    .withLandsOnBattlefield(1, "Island", 5)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Quantum Riddler")
                cast.error shouldBe null
                game.resolveStack()

                // After casting Riddler, hand = {Plains, Swamp} (size 2). When the ETB-draw
                // trigger resolves, hand-size > 1, so the modifier does NOT apply: draw 1.
                withClue("Hand should hold the original 2 cards plus 1 drawn (no modifier)") {
                    game.handSize(1) shouldBe 3
                }
            }

            test("draw-step draw is modified — turn 2 with hand = 0 draws 2 instead of 1") {
                // Regression for the second announcement site wired into the modifier:
                // DrawPhaseManager.performDrawStep. Turn 2 (not first-turn-first-player so the
                // step isn't skipped), Quantum Riddler already on the battlefield, an empty
                // hand → the +1 modifier applies and the draw step draws 2.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Quantum Riddler", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Hill Giant")
                    .withCardInLibrary(1, "Mountain")
                    .withActivePlayer(1)
                    .withTurnNumber(2)
                    .inPhase(Phase.BEGINNING, Step.UPKEEP)
                    .build()

                game.handSize(1) shouldBe 0
                game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

                withClue("Draw step with hand-size = 0 should draw 1 + 1 = 2") {
                    game.handSize(1) shouldBe 2
                }
            }

            test("cycling's 'Draw a card' is modified when the hand-size restriction holds") {
                // Regression for the third announcement site wired into the modifier:
                // CycleCardHandler. Cycling is "Discard this, Draw a card" (CR 702.29a), so
                // its draw is its own announcement site — Quantum Riddler must add +1 when
                // its restriction holds. Hundroog ({6}{G}, cycling {3}) is registered via
                // LegionsSet; with only Hundroog in hand, the post-discard hand is empty and
                // the cycle draws 2.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Quantum Riddler", summoningSickness = false)
                    .withCardInHand(1, "Hundroog")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Hill Giant")
                    .withCardInLibrary(1, "Mountain")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cycle = game.cycleCard(1, "Hundroog")
                withClue("Cycle should succeed: ${cycle.error}") { cycle.error shouldBe null }
                game.resolveStack()

                withClue("Cycling with post-discard hand-size = 0 should draw 1 + 1 = 2") {
                    game.handSize(1) shouldBe 2
                }
            }
        }
    }
}
