package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Sonic Shrieker (#226) and its new [com.wingedsheep.sdk.scripting.conditions.TargetIsPlayer]
 * condition.
 *
 * ETB: "deals 2 damage to any target and you gain 2 life. If a player is dealt damage this way,
 * they discard a card." The discard rider is gated on the damaged "any target" having been a
 * player and aimed back at that same player via ContextTarget.
 */
class SonicShriekerScenarioTest : ScenarioTestBase() {

    init {
        context("Sonic Shrieker ETB") {
            test("targets an opponent: 2 damage to the player, caster gains 2 life, the player discards") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Sonic Shrieker")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withCardInHand(2, "Grizzly Bears") // a card to discard
                    .withLifeTotal(1, 20)
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Sonic Shrieker")
                withClue("Casting the creature should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack() // creature enters → ETB trigger asks for its "any target"

                val targeted = game.selectTargets(listOf(game.player2Id))
                withClue("Targeting Player 2 should be legal: ${targeted.error}") { targeted.error shouldBe null }
                game.resolveStack()

                withClue("Player 2 takes 2 damage (20 -> 18)") {
                    game.getLifeTotal(2) shouldBe 18
                }
                withClue("Caster gains 2 life (20 -> 22)") {
                    game.getLifeTotal(1) shouldBe 22
                }
                withClue("Player 2 was dealt damage and discards their card") {
                    game.state.getHand(game.player2Id).size shouldBe 0
                }
            }

            test("targets a creature: no player damaged, so no discard happens") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Sonic Shrieker")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withCardOnBattlefield(2, "Hill Giant") // 3/3, survives 2 damage
                    .withCardInHand(2, "Grizzly Bears") // should NOT be discarded
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Sonic Shrieker")
                withClue("Casting the creature should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack() // creature enters → ETB trigger asks for its "any target"

                val hillGiant = game.findPermanent("Hill Giant")!!
                val targeted = game.selectTargets(listOf(hillGiant))
                withClue("Targeting Hill Giant should be legal: ${targeted.error}") { targeted.error shouldBe null }
                game.resolveStack()

                withClue("Hill Giant (3/3) survives 2 damage") {
                    game.isOnBattlefield("Hill Giant") shouldBe true
                }
                withClue("Caster gains 2 life (20 -> 22)") {
                    game.getLifeTotal(1) shouldBe 22
                }
                withClue("No player was dealt damage → Player 2 keeps their card") {
                    game.state.getHand(game.player2Id).size shouldBe 1
                }
            }
        }
    }
}
