package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Reverse Polarity — "You gain X life, where X is twice the damage dealt to you so far this turn
 * by artifacts."
 *
 * Proves the per-turn artifact-source damage tracker
 * (`TurnTracker.DAMAGE_RECEIVED_FROM_ARTIFACTS`) feeding `Multiply(..., 2)`:
 *  - after an artifact creature deals 1 combat damage to you, the spell gains 2 (twice that),
 *  - after only non-artifact damage, it gains 0 (non-artifact damage is not counted).
 */
class ReversePolarityScenarioTest : ScenarioTestBase() {

    init {
        context("Reverse Polarity doubles artifact-source damage taken this turn") {
            test("gains twice the artifact combat damage dealt to you") {
                // P2 attacks P1 with Yotian Soldier (1/4 ARTIFACT creature) for 1 artifact damage.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withLifeTotal(1, 20)
                    .withCardInHand(1, "Reverse Polarity")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withCardOnBattlefield(2, "Yotian Soldier", summoningSickness = false)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                val attack = game.declareAttackers(mapOf("Yotian Soldier" to 1))
                withClue("Declaring the attack should succeed: ${attack.error}") { attack.error shouldBe null }
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                game.passPriority() // P2 passes → P1 gets priority to cast the instant

                withClue("P1 took 1 combat damage from the artifact creature") {
                    game.getLifeTotal(1) shouldBe 19
                }

                val cast = game.castSpell(1, "Reverse Polarity")
                withClue("Reverse Polarity should cast: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                withClue("Gains twice the 1 artifact damage = 2 (19 + 2 = 21)") {
                    game.getLifeTotal(1) shouldBe 21
                }
            }

            test("gains 0 when the damage came from a non-artifact source") {
                // P2 attacks P1 with Grizzly Bears (2/2 non-artifact) for 2 non-artifact damage.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withLifeTotal(1, 20)
                    .withCardInHand(1, "Reverse Polarity")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                val attack = game.declareAttackers(mapOf("Grizzly Bears" to 1))
                withClue("Declaring the attack should succeed: ${attack.error}") { attack.error shouldBe null }
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                game.passPriority() // P2 passes → P1 gets priority to cast the instant

                withClue("P1 took 2 combat damage from the non-artifact creature") {
                    game.getLifeTotal(1) shouldBe 18
                }

                val cast = game.castSpell(1, "Reverse Polarity")
                withClue("Reverse Polarity should cast: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                withClue("No artifact damage this turn, so X = 0; life unchanged at 18") {
                    game.getLifeTotal(1) shouldBe 18
                }
            }
        }
    }
}
