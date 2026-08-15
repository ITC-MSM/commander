package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.mana.CostCalculator
import com.wingedsheep.engine.support.ScenarioTestBase
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Hellspur Brute's "Affinity for outlaws".
 *
 * Oracle: "Affinity for outlaws (This spell costs {1} less to cast for each Assassin, Mercenary,
 * Pirate, Rogue, and/or Warlock you control.)"
 *
 * Modeled as a self-cast ModifySpellCost whose generic reduction counts permanents you control
 * matching the OUTLAW_TYPES creature filter. We verify the effective cost directly via the
 * CostCalculator, scaling the reduction with the number of outlaws controlled.
 */
class HellspurBruteScenarioTest : ScenarioTestBase() {

    init {
        context("Hellspur Brute — Affinity for outlaws") {

            test("no outlaws controlled → full {4}{R} cost (4 generic)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Hellspur Brute")
                    .build()

                val calculator = CostCalculator(cardRegistry)
                val cost = calculator.calculateEffectiveCost(
                    game.state,
                    cardRegistry.requireCard("Hellspur Brute"),
                    game.player1Id,
                )

                withClue("with no outlaws, the generic component stays at 4") {
                    cost.genericAmount shouldBe 4
                }
            }

            test("two outlaws controlled → cost reduced by 2 (2 generic)") {
                // Reckless Lackey is a Goblin Pirate (Pirate = outlaw); Outlaw Medic is a Human
                // Rogue (Rogue = outlaw). Two outlaws → {4}{R} reduced to {2}{R}.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Hellspur Brute")
                    .withCardOnBattlefield(1, "Reckless Lackey")
                    .withCardOnBattlefield(1, "Outlaw Medic")
                    .build()

                val calculator = CostCalculator(cardRegistry)
                val cost = calculator.calculateEffectiveCost(
                    game.state,
                    cardRegistry.requireCard("Hellspur Brute"),
                    game.player1Id,
                )

                withClue("two controlled outlaws reduce the generic from 4 to 2") {
                    cost.genericAmount shouldBe 2
                }
            }

            test("only the caster's own outlaws count, not the opponent's") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Hellspur Brute")
                    .withCardOnBattlefield(2, "Reckless Lackey") // opponent's outlaw — must NOT count
                    .build()

                val calculator = CostCalculator(cardRegistry)
                val cost = calculator.calculateEffectiveCost(
                    game.state,
                    cardRegistry.requireCard("Hellspur Brute"),
                    game.player1Id,
                )

                withClue("an opponent's outlaw provides no affinity discount") {
                    cost.genericAmount shouldBe 4
                }
            }
        }
    }
}
