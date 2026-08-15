package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Disciple of Law (USG) — {1}{W} Creature — Human Cleric, 1/2.
 *
 * "Protection from red
 *  Cycling {2} ({2}, Discard this card: Draw a card.)"
 *
 * Exercises the Cycling keyword ability (CR 702.29) mapped through the mtgish
 * `_Rule = Cycling` bridge entry: `KeywordAbility.cycling("{2}")` synthesises the
 * "Discard this card: Draw a card" activated ability that can be played from hand.
 */
class DiscipleOfLawTest : ScenarioTestBase() {

    init {
        context("Disciple of Law's cycling ability") {

            test("cycling pays {2}, discards the card, and draws a replacement") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Disciple of Law")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.isInHand(1, "Disciple of Law") shouldBe true

                val cycle = game.cycleCard(1, "Disciple of Law")
                withClue("Cycling should succeed: ${cycle.error}") { cycle.error shouldBe null }
                game.resolveStack()

                withClue("Cycled card is discarded to the graveyard") {
                    game.isInGraveyard(1, "Disciple of Law") shouldBe true
                    game.isInHand(1, "Disciple of Law") shouldBe false
                }
                withClue("Cycling draws the next card off the library") {
                    game.isInHand(1, "Grizzly Bears") shouldBe true
                }
            }

            test("cycling fails without the mana to pay its cost") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Disciple of Law")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.cycleCard(1, "Disciple of Law")

                // With no mana available the cost can't be paid, so the card is neither
                // discarded nor cycled — it stays in hand and the graveyard stays empty.
                withClue("Cycling does not resolve without the mana to pay {2}") {
                    game.isInHand(1, "Disciple of Law") shouldBe true
                    game.isInGraveyard(1, "Disciple of Law") shouldBe false
                }
            }
        }
    }
}
