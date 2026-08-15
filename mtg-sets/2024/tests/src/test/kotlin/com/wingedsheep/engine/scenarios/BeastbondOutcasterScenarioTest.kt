package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Beastbond Outcaster's enter-the-battlefield draw trigger.
 *
 * Oracle: "When this creature enters, if you control a creature with power 4 or greater, draw a
 * card." (Plus Plot {1}{G}, which is the named keyword and not exercised here.)
 *
 * Intervening-if (CR 603.4): the draw only happens if you control a creature with power >= 4 when
 * the trigger resolves. Beastbond Outcaster itself is a 3/3, so it doesn't satisfy its own gate.
 */
class BeastbondOutcasterScenarioTest : ScenarioTestBase() {

    init {
        context("Beastbond Outcaster — ETB draw if you control a 4-power creature") {

            test("draws a card when a creature with power 4 or greater is already in play") {
                // Outcaster Trailblazer is a 4/2 (power 4) creature.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Beastbond Outcaster")
                    .withCardOnBattlefield(1, "Outcaster Trailblazer")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withActivePlayer(1)
                    .build()

                val libBefore = game.state.getLibrary(game.player1Id).size

                game.castSpell(1, "Beastbond Outcaster").error shouldBe null
                game.resolveStack()

                withClue("ETB sees a power-4 creature → draw one card (library -1)") {
                    game.state.getLibrary(game.player1Id).size shouldBe libBefore - 1
                }
            }

            test("draws nothing when no creature has power 4 or greater") {
                // Beastbond Outcaster is only a 3/3; no other creature in play.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Beastbond Outcaster")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withActivePlayer(1)
                    .build()

                val libBefore = game.state.getLibrary(game.player1Id).size

                game.castSpell(1, "Beastbond Outcaster").error shouldBe null
                game.resolveStack()

                withClue("no power-4 creature → intervening-if fails, no draw") {
                    game.state.getLibrary(game.player1Id).size shouldBe libBefore
                }
            }
        }
    }
}
