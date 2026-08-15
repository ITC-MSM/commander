package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Frontier Seeker (OTJ #13) — {1}{W} Creature — Human Scout, 2/1.
 *
 * "When this creature enters, look at the top five cards of your library. You may reveal a
 *  Mount creature card or a Plains card from among them and put it into your hand. Put the
 *  rest on the bottom of your library in a random order."
 *
 * Exercises the look-at-top / may-take-a-matching-card / bottom-the-rest pipeline: only a
 * Mount creature card or a Plains card is a legal pick; everything else returns to the bottom.
 */
class FrontierSeekerScenarioTest : ScenarioTestBase() {

    init {
        context("Frontier Seeker enters-the-battlefield trigger") {

            fun buildGame() = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Frontier Seeker")
                .withLandsOnBattlefield(1, "Plains", 2)
                // Top of library seeded with a Mount creature, a non-matching creature, and filler.
                .withCardInLibrary(1, "Bridled Bighorn") // Sheep Mount — a legal pick
                .withCardInLibrary(1, "Grizzly Bears")   // not a Mount, not a Plains
                .withCardInLibrary(1, "Mountain")
                .withCardInLibrary(1, "Mountain")
                .withCardInLibrary(1, "Mountain")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            test("takes the Mount creature card into hand and bottoms the rest") {
                val game = buildGame()
                val mount = game.findCardsInLibrary(1, "Bridled Bighorn").first()

                game.castSpell(1, "Frontier Seeker").error shouldBe null
                game.resolveStack()

                // ETB look-5 pauses for a "may take a Mount/Plains card" selection.
                game.getPendingDecision() ?: error("expected a selection decision for the look-five trigger")
                game.selectCards(listOf(mount)).error shouldBe null
                game.resolveStack()

                withClue("the Mount creature card is now in hand") {
                    game.isInHand(1, "Bridled Bighorn") shouldBe true
                }
                withClue("the other four looked-at cards went to the bottom (library back to 4)") {
                    game.librarySize(1) shouldBe 4
                }
                withClue("Frontier Seeker resolved onto the battlefield") {
                    game.isOnBattlefield("Frontier Seeker") shouldBe true
                }
            }

            test("may decline and put all five on the bottom") {
                val game = buildGame()

                game.castSpell(1, "Frontier Seeker").error shouldBe null
                game.resolveStack()

                game.getPendingDecision() ?: error("expected a selection decision for the look-five trigger")
                // Decline the optional reveal — nothing goes to hand.
                game.skipSelection().error shouldBe null
                game.resolveStack()

                withClue("nothing added to hand") {
                    game.isInHand(1, "Bridled Bighorn") shouldBe false
                }
                withClue("all five looked-at cards returned to the bottom") {
                    game.librarySize(1) shouldBe 5
                }
            }
        }
    }
}
