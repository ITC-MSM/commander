package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Mine Raider's enter-the-battlefield Treasure trigger.
 *
 * Oracle: "When this creature enters, if you control another outlaw, create a Treasure token."
 *
 * Mine Raider is itself a Human Rogue (Rogue = outlaw), so "you control another outlaw" is
 * modeled as YouControlAtLeast(2, OUTLAW_TYPES creatures): Mine Raider plus at least one other.
 */
class MineRaiderScenarioTest : ScenarioTestBase() {

    private fun treasureCount(game: TestGame): Int = game.findPermanents("Treasure").size

    init {
        context("Mine Raider — Treasure on ETB if you control another outlaw") {

            test("creates a Treasure when cast with another outlaw in play") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Mine Raider")
                    .withCardOnBattlefield(1, "Outlaw Medic") // another outlaw (Human Rogue)
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withActivePlayer(1)
                    .build()

                withClue("no Treasure before Mine Raider resolves") {
                    treasureCount(game) shouldBe 0
                }

                game.castSpell(1, "Mine Raider").error shouldBe null
                game.resolveStack()

                withClue("ETB sees another outlaw (Outlaw Medic) → one Treasure created") {
                    treasureCount(game) shouldBe 1
                }
            }

            test("creates no Treasure when no other outlaw is in play") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Mine Raider")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withActivePlayer(1)
                    .build()

                game.castSpell(1, "Mine Raider").error shouldBe null
                game.resolveStack()

                withClue("Mine Raider is the only outlaw → no Treasure") {
                    treasureCount(game) shouldBe 0
                }
            }
        }
    }
}
