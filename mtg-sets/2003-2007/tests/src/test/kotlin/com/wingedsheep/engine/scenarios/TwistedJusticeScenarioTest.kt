package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Twisted Justice (Ravnica: City of Guilds).
 *
 * Oracle: "Target player sacrifices a creature of their choice. You draw cards equal to that
 * creature's power."
 *
 * The seam under test is the one between the two sentences: the creature is in the graveyard by the
 * time the draw counts its power, so the count can only come from the last-known-information
 * snapshot the sacrifice captured (CR 608.2h). If that snapshot were missed the card would silently
 * draw zero, which is why the interesting assertion is the *card count*, not the sacrifice.
 */
class TwistedJusticeScenarioTest : ScenarioTestBase() {

    init {
        context("Twisted Justice — sacrifice, then draw that creature's power") {

            test("draws cards equal to the sacrificed creature's power") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Twisted Justice")
                    .withLandsOnBattlefield(1, "Island", 5)
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withCardOnBattlefield(2, "Siege Wurm")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)
                game.castSpellTargetingPlayer(1, "Twisted Justice", 2).error shouldBe null
                game.resolveStack()

                withClue("the target player sacrificed their only creature") {
                    game.isOnBattlefield("Siege Wurm") shouldBe false
                    game.isInGraveyard(2, "Siege Wurm") shouldBe true
                }
                withClue("Siege Wurm is a 5/5, so five cards — read from the LKI snapshot") {
                    // handBefore counts Twisted Justice itself, which left the hand to be cast.
                    game.handSize(1) shouldBe handBefore - 1 + 5
                }
            }

            test("a target player with no creatures sacrifices nothing and draws nothing") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Twisted Justice")
                    .withLandsOnBattlefield(1, "Island", 5)
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)
                game.castSpellTargetingPlayer(1, "Twisted Justice", 2).error shouldBe null
                game.resolveStack()

                withClue("nothing sacrificed → the amount is 0, and the spell still resolved") {
                    game.handSize(1) shouldBe handBefore - 1
                    game.isInGraveyard(1, "Twisted Justice") shouldBe true
                }
            }
        }
    }
}
