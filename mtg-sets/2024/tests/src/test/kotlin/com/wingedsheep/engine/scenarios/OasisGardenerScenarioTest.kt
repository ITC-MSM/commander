package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Oasis Gardener (OTJ #246) — {3} Artifact Creature — Scarecrow, 2/2.
 *
 * "When this creature enters, you gain 2 life.
 *  {T}: Add one mana of any color."
 *
 * The mana ability is a generic, well-covered primitive ([Effects.AddAnyColorMana]); this test
 * pins the novel ETB life gain and confirms the creature resolves onto the battlefield.
 */
class OasisGardenerScenarioTest : ScenarioTestBase() {

    init {
        context("Oasis Gardener ETB") {

            test("gains 2 life when it enters") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Oasis Gardener")
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val startLife = game.getLifeTotal(1)

                game.castSpell(1, "Oasis Gardener").error shouldBe null
                game.resolveStack()

                withClue("controller gained 2 life from the ETB trigger") {
                    game.getLifeTotal(1) shouldBe startLife + 2
                }
                withClue("Oasis Gardener resolved onto the battlefield") {
                    game.isOnBattlefield("Oasis Gardener") shouldBe true
                }
            }
        }
    }
}
