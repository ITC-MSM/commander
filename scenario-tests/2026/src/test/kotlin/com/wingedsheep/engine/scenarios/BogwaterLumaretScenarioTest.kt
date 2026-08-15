package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Bogwater Lumaret (Secrets of Strixhaven #177).
 *
 * Bogwater Lumaret ({B}{G}, 2/2 Spirit Frog):
 *   Whenever this creature or another creature you control enters, you gain 1 life.
 */
class BogwaterLumaretScenarioTest : ScenarioTestBase() {

    init {
        context("Bogwater Lumaret — gain 1 life on creature ETB") {

            test("gains 1 life when it enters the battlefield itself") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Bogwater Lumaret")
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Bogwater Lumaret").error shouldBe null
                game.resolveStack()

                withClue("its own ETB triggers the 1 life gain") {
                    game.getLifeTotal(1) shouldBe 21
                }
            }

            test("gains 1 life when another creature you control enters") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Bogwater Lumaret")
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Grizzly Bears").error shouldBe null
                game.resolveStack()

                withClue("another creature's ETB triggers the 1 life gain") {
                    game.getLifeTotal(1) shouldBe 21
                }
            }

            test("does NOT gain life when an opponent's creature enters") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Bogwater Lumaret")
                    .withCardInHand(2, "Grizzly Bears")
                    .withLandsOnBattlefield(2, "Forest", 2)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(2, "Grizzly Bears").error shouldBe null
                game.resolveStack()

                withClue("opponent's creature ETB does not trigger Bogwater (you control filter)") {
                    game.getLifeTotal(1) shouldBe 20
                }
            }
        }
    }
}
