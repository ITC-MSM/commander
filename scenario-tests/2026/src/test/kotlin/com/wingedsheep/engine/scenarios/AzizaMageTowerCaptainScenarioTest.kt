package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Aziza, Mage Tower Captain (Secrets of Strixhaven #174).
 *
 * Aziza ({R}{W} Legendary Creature, 2/2):
 *   Whenever you cast an instant or sorcery spell, you may tap three untapped creatures you
 *   control. If you do, copy that spell. You may choose new targets for the copy.
 *
 * Exercises the cast-trigger -> optional tap-three cost -> copy-the-spell composition: paying the
 * cost copies the spell (a Shock at the opponent resolves twice for 4 total), and declining leaves
 * a single resolution.
 */
class AzizaMageTowerCaptainScenarioTest : ScenarioTestBase() {

    init {
        context("Aziza, Mage Tower Captain") {

            test("paying the tap-three cost copies the cast instant") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Aziza, Mage Tower Captain")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Shock")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val startingLife = game.getLifeTotal(2)

                game.castSpellTargetingPlayer(1, "Shock", targetPlayerNumber = 2).error shouldBe null

                // Aziza's trigger resolves first: answer "yes" to the may-pay, then tap three creatures.
                game.resolveStack()
                game.answerYesNo(true)
                val bears = game.findPermanents("Grizzly Bears")
                game.selectCards(bears.take(3))
                // The copy is created and pauses for "choose new targets" — keep the opponent.
                game.resolveStack()
                val copyDecision = game.state.pendingDecision
                withClue("The copy pauses to (optionally) choose new targets") {
                    (copyDecision is ChooseTargetsDecision) shouldBe true
                }
                game.selectTargets(listOf(game.player2Id))
                game.resolveStack()

                withClue("Shock + its Aziza copy each deal 2 to the opponent (4 total)") {
                    game.getLifeTotal(2) shouldBe startingLife - 4
                }
            }

            test("declining the tap cost resolves the instant only once") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Aziza, Mage Tower Captain")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Shock")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val startingLife = game.getLifeTotal(2)

                game.castSpellTargetingPlayer(1, "Shock", targetPlayerNumber = 2).error shouldBe null
                game.resolveStack()
                game.answerYesNo(false)
                game.resolveStack()

                withClue("Declining means no copy — only the original Shock deals 2") {
                    game.getLifeTotal(2) shouldBe startingLife - 2
                }
            }
        }
    }
}
