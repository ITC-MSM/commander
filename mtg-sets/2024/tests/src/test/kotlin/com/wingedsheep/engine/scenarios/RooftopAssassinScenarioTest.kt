package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Rooftop Assassin (OTJ #103).
 *
 * "{3}{B} Creature — Vampire Assassin 2/2. Flash. Flying, lifelink.
 *  When this creature enters, destroy target creature an opponent controls that was dealt
 *  damage this turn."
 *
 * Verifies the ETB destroys a damaged opponent creature, and that an undamaged opponent
 * creature is not a legal target (so the trigger has no target).
 */
class RooftopAssassinScenarioTest : ScenarioTestBase() {

    init {
        context("Rooftop Assassin") {

            test("ETB destroys an opponent creature that was dealt damage this turn") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Rooftop Assassin")
                    .withCardInHand(1, "Shock")
                    .withCardOnBattlefield(2, "Hill Giant") // 3/3, survives Shock's 2 damage
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val giant = game.findPermanent("Hill Giant")!!

                // Shock the Giant so it is "dealt damage this turn" but survives (3 toughness).
                game.castSpell(1, "Shock", giant).error shouldBe null
                game.resolveStack()
                withClue("Hill Giant survives the Shock") {
                    game.isOnBattlefield("Hill Giant") shouldBe true
                }

                // Flash in Rooftop Assassin and destroy the damaged Giant.
                game.castSpell(1, "Rooftop Assassin").error shouldBe null
                game.resolveStack() // creature enters -> ETB asks for a target
                game.selectTargets(listOf(giant)).error shouldBe null
                game.resolveStack()

                withClue("Hill Giant is destroyed by the ETB") {
                    game.isOnBattlefield("Hill Giant") shouldBe false
                }
                withClue("Rooftop Assassin is on the battlefield") {
                    game.isOnBattlefield("Rooftop Assassin") shouldBe true
                }
            }

            test("an undamaged opponent creature is not a legal target") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Rooftop Assassin")
                    .withCardOnBattlefield(2, "Hill Giant") // 3/3, never damaged
                    .withLandsOnBattlefield(1, "Swamp", 6)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Rooftop Assassin").error shouldBe null
                game.resolveStack()

                withClue("No legal target exists, so the Giant is untouched") {
                    game.isOnBattlefield("Hill Giant") shouldBe true
                }
                withClue("Rooftop Assassin still enters the battlefield") {
                    game.isOnBattlefield("Rooftop Assassin") shouldBe true
                }
            }
        }
    }
}
