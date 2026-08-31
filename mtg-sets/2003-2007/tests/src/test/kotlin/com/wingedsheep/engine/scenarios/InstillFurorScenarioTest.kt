package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Instill Furor (Ravnica: City of Guilds).
 *
 * Oracle: "Enchant creature / Enchanted creature has \"At the beginning of your end step, sacrifice
 * this creature unless it attacked this turn.\""
 *
 * Three claims the composition makes, each provable:
 *
 *  1. The granted ability's "this creature" is the *enchanted* creature, not the Aura — an idle host
 *     is sacrificed at its controller's end step.
 *  2. "Unless it attacked this turn" is read at resolution: a host that attacked survives.
 *  3. The trigger belongs to the *host's* controller, per the 2005-10-01 ruling — the last two tests
 *     put the Aura on an opponent's creature and run the same board on each player's end step: the
 *     Aura controller's does nothing, the host controller's sacrifices it.
 */
class InstillFurorScenarioTest : ScenarioTestBase() {

    init {
        context("Instill Furor — sacrifice unless it attacked") {

            test("an idle enchanted creature is sacrificed at its controller's end step") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Instill Furor", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("it did not attack, so the granted ability sacrifices it") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                }
            }

            test("an enchanted creature that attacked this turn survives") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Instill Furor", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("the 'unless' clause spares an attacker") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
            }

            test("nothing happens on the Aura controller's end step when the host is an opponent's") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardAttachedTo(1, "Instill Furor", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("player 1's end step is not the host controller's end step") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
            }

            test("the host is sacrificed on its own controller's end step, not the Aura controller's") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardAttachedTo(1, "Instill Furor", "Grizzly Bears")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("the granted ability is controlled by the host's controller (2005-10-01 ruling)") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe true
                }
            }
        }
    }
}
