package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Venerated Stormsinger (TDM #97) — {3}{B} Orc Cleric, 3/3.
 *
 * "Whenever this creature or another creature you control dies, each opponent loses 1 life and
 *  you gain 1 life."
 *
 * Uses Caustic Exhale (-3/-3) to kill a creature the controller owns and verifies the drain
 * triggers once per death (opponent loses 1, controller gains 1). Also verifies it fires when
 * the Stormsinger itself dies.
 */
class VeneratedStormsingerScenarioTest : ScenarioTestBase() {

    init {
        context("Venerated Stormsinger dies-drain") {

            test("another creature you control dying drains 1 / gains 1") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Venerated Stormsinger")
                    .withCardOnBattlefield(1, "Grizzly Bears") // 2/2
                    .withCardInHand(1, "Caustic Exhale")
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withLifeTotal(1, 20)
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val cast = game.castSpell(1, "Caustic Exhale", bears)
                withClue("Casting Caustic Exhale on Grizzly Bears should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("Grizzly Bears dies to -3/-3") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                }
                withClue("Each opponent loses 1 life from the dies trigger") {
                    game.getLifeTotal(2) shouldBe 19
                }
                withClue("Controller gains 1 life from the dies trigger") {
                    game.getLifeTotal(1) shouldBe 21
                }
            }

            test("the Stormsinger dying also triggers its own drain") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Venerated Stormsinger")
                    .withCardInHand(1, "Caustic Exhale")
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withLifeTotal(1, 20)
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val stormsinger = game.findPermanent("Venerated Stormsinger")!!
                val cast = game.castSpell(1, "Caustic Exhale", stormsinger)
                withClue("Casting Caustic Exhale on the Stormsinger should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("Venerated Stormsinger dies to -3/-3") {
                    game.isOnBattlefield("Venerated Stormsinger") shouldBe false
                }
                withClue("Its own death triggers the drain: opponent loses 1") {
                    game.getLifeTotal(2) shouldBe 19
                }
                withClue("Its own death triggers the drain: controller gains 1") {
                    game.getLifeTotal(1) shouldBe 21
                }
            }
        }
    }
}
