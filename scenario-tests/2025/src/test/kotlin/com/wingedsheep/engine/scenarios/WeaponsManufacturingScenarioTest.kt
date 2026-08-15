package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Weapons Manufacturing.
 *
 * Card reference:
 * - Weapons Manufacturing ({1}{R}): Enchantment
 *   "Whenever a nontoken artifact you control enters, create a colorless artifact
 *    token named Munitions with 'When this token leaves the battlefield, it deals
 *    2 damage to any target.'"
 *
 * The Munitions token is a colorless non-creature artifact token with a leaves-the-
 * battlefield triggered ability — the first predefined token using this shape.
 */
class WeaponsManufacturingScenarioTest : ScenarioTestBase() {

    init {
        context("Weapons Manufacturing") {

            test("creates a Munitions token when a nontoken artifact enters") {
                val game = scenario()
                    .withPlayers("Manufacturer", "Opponent")
                    .withCardOnBattlefield(1, "Weapons Manufacturing")
                    .withCardInHand(1, "Fellwar Stone")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.isOnBattlefield("Munitions") shouldBe false

                val cast = game.castSpell(1, "Fellwar Stone")
                withClue("Fellwar Stone should be cast successfully: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("Fellwar Stone should be on the battlefield") {
                    game.isOnBattlefield("Fellwar Stone") shouldBe true
                }
                withClue("Weapons Manufacturing should have created a Munitions token") {
                    game.isOnBattlefield("Munitions") shouldBe true
                }
            }

            test("Munitions token deals 2 damage to a player when it leaves the battlefield") {
                val game = scenario()
                    .withPlayers("Manufacturer", "Opponent")
                    .withCardOnBattlefield(1, "Weapons Manufacturing")
                    .withCardInHand(1, "Fellwar Stone")
                    .withCardInHand(1, "Naturalize")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Fellwar Stone")
                game.resolveStack()
                game.isOnBattlefield("Munitions") shouldBe true

                val munitionsId = game.findPermanent("Munitions")!!
                val destroy = game.castSpell(1, "Naturalize", munitionsId)
                withClue("Naturalize should target the Munitions token: ${destroy.error}") {
                    destroy.error shouldBe null
                }
                game.resolveStack() // Naturalize resolves → Munitions LTB triggers, asks for target

                withClue("Munitions LTB should request a target") {
                    game.hasPendingDecision() shouldBe true
                }
                game.selectTargets(listOf(game.player2Id))
                game.resolveStack()

                withClue("Munitions should be gone") {
                    game.isOnBattlefield("Munitions") shouldBe false
                }
                withClue("Opponent should have taken 2 damage from Munitions LTB") {
                    game.getLifeTotal(2) shouldBe 18
                }
            }

            test("does not create a Munitions token when a token artifact enters") {
                val game = scenario()
                    .withPlayers("Manufacturer", "Opponent")
                    .withCardOnBattlefield(1, "Weapons Manufacturing")
                    .withCardOnBattlefield(1, "Mechan Assembler") // Whenever another artifact enters, create a Robot token (once per turn)
                    .withCardInHand(1, "Fellwar Stone")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Fellwar Stone")
                game.resolveStack()

                // Fellwar Stone is the only nontoken artifact entering — exactly one Munitions
                // should be created. The subsequent Robot token (created by Mechan Assembler)
                // is a token and must NOT re-trigger Weapons Manufacturing.
                withClue("Exactly one Munitions token should exist (token Robot does not re-trigger WM)") {
                    game.findPermanents("Munitions").size shouldBe 1
                }
            }
        }
    }
}
