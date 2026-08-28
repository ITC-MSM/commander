package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Change of Fortune (VOW #150) — {3}{R} Sorcery.
 *
 *   Discard your hand, then draw a card for each card you've discarded this turn.
 *
 * The whole card is its ordering: the discard has to land in the turn's discard tally *before*
 * the draw amount is read, or the spell draws zero. Asserted directly, since a `Composite` whose
 * halves were swapped would still compile and still pass every structural net.
 */
class ChangeOfFortuneScenarioTest : ScenarioTestBase() {

    init {
        context("Change of Fortune") {

            test("discards the hand and draws that many cards") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Change of Fortune")
                    .withCardInHand(1, "Grizzly Bears")
                    .withCardInHand(1, "Hill Giant")
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Change of Fortune").error shouldBe null
                game.resolveStack()

                withClue("the three remaining cards were discarded, then three were drawn") {
                    game.handSize(1) shouldBe 3
                }
                withClue("the discarded cards are in the graveyard alongside the spell") {
                    game.isInGraveyard(1, "Hill Giant") shouldBe true
                    game.isInGraveyard(1, "Lightning Bolt") shouldBe true
                }
            }

            test("an empty hand draws nothing") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Change of Fortune")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val librarySizeBefore = game.librarySize(1)

                game.castSpell(1, "Change of Fortune").error shouldBe null
                game.resolveStack()

                withClue("nothing was discarded, so nothing is drawn") {
                    game.handSize(1) shouldBe 0
                    game.librarySize(1) shouldBe librarySizeBefore
                }
            }
        }
    }
}
