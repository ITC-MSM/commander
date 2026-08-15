package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Enduring Tenacity.
 *
 * Enduring Tenacity ({2}{B}{B}): Enchantment Creature — Snake Glimmer, 4/3
 * - Whenever you gain life, target opponent loses that much life.
 * - Enduring: when it dies (as a creature) it returns as a (non-creature) enchantment.
 *
 * The Enduring death clause is covered by EnduringMechanicTest; here we prove the
 * unique life-drain trigger, that the amount drained equals the life actually gained
 * ("that much"), and that an opponent gaining life does not trigger it.
 */
class EnduringTenacityScenarioTest : ScenarioTestBase() {

    init {
        context("Enduring Tenacity drains an opponent when you gain life") {

            test("gaining 6 life drains the opponent for 6") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Enduring Tenacity")
                    .withCardInHand(1, "Rejuvenate")
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withLifeTotal(1, 20)
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Rejuvenate: "You gain 6 life." (untargeted, sorcery-speed)
                game.castSpell(1, "Rejuvenate").error shouldBe null
                game.resolveStack()

                withClue("Controller gained 6 life: 20 -> 26") {
                    game.getLifeTotal(1) shouldBe 26
                }
                withClue("Target opponent lost that much (6): 20 -> 14") {
                    game.getLifeTotal(2) shouldBe 14
                }
            }

            test("an opponent gaining life does not trigger Tenacity") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Enduring Tenacity")
                    .withCardInHand(2, "Rejuvenate")
                    .withLandsOnBattlefield(2, "Forest", 4)
                    .withLifeTotal(1, 20)
                    .withLifeTotal(2, 20)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Player 2 (the opponent) gains the life; Tenacity reads "Whenever YOU gain life".
                game.castSpell(2, "Rejuvenate").error shouldBe null
                game.resolveStack()

                withClue("Opponent gained 6 life: 20 -> 26") {
                    game.getLifeTotal(2) shouldBe 26
                }
                withClue("Tenacity's controller is unaffected — the trigger is yours, not theirs") {
                    game.getLifeTotal(1) shouldBe 20
                }
            }
        }
    }
}
