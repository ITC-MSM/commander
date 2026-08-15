package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * High-Society Hunter (FDN) — {3}{B}{B} 5/3 Vampire Noble.
 * Flying; attack trigger (may sacrifice another creature for a +1/+1 counter);
 * "Whenever another nontoken creature dies, draw a card."
 *
 * Exercises the another-nontoken-creature-dies trigger (leavesBattlefield → graveyard,
 * OTHER binding, nontoken filter) wired to a draw.
 */
class HighSocietyHunterScenarioTest : ScenarioTestBase() {

    init {
        context("High-Society Hunter's death trigger") {

            test("draws a card when another nontoken creature dies") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "High-Society Hunter")
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardInLibrary(1, "Swamp")
                    // The doomed nontoken creature belongs to the opponent — any controller counts.
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                // Bolt (3 damage) kills the 2/2 Grizzly Bears.
                game.castSpell(1, "Lightning Bolt", targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("Grizzly Bears died") {
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe true
                }
                // Player1 hand: started with Bolt (1) → cast empties it (0) → death trigger draws 1.
                withClue("High-Society Hunter's controller drew exactly one card") {
                    game.handSize(1) shouldBe 1
                }
            }
        }
    }
}
