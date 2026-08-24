package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.mana.CostCalculator
import com.wingedsheep.engine.support.ScenarioTestBase
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Cryptic Serpent's self-cost reduction.
 *
 * Oracle: "This spell costs {1} less to cast for each instant and sorcery card in your graveyard."
 *
 * Base cost is {5}{U}{U} — 5 generic plus two blue pips. Only the generic portion is reduced, once
 * per *card* (not once per card type), and only for cards in the caster's own graveyard.
 */
class CrypticSerpentScenarioTest : ScenarioTestBase() {

    private fun genericCostFor(game: TestGame): Int =
        CostCalculator(cardRegistry).calculateEffectiveCost(
            game.state,
            cardRegistry.requireCard("Cryptic Serpent"),
            game.player1Id,
        ).genericAmount

    init {
        context("Cryptic Serpent — cost reduction") {

            test("empty graveyard → full 5 generic") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Cryptic Serpent")
                    .build()

                withClue("with no instants or sorceries the generic component stays at 5") {
                    genericCostFor(game) shouldBe 5
                }
            }

            test("one instant and one sorcery → generic reduced by 2") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Cryptic Serpent")
                    .withCardInGraveyard(1, "Shock")        // instant
                    .withCardInGraveyard(1, "Divination")   // sorcery
                    .build()

                withClue("two matching cards reduce generic from 5 to 3") {
                    genericCostFor(game) shouldBe 3
                }
            }

            test("counts cards, not card types — three instants reduce by 3") {
                val builder = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Cryptic Serpent")
                repeat(3) { builder.withCardInGraveyard(1, "Shock") }
                val game = builder.build()

                withClue("three instant cards give {3} off, not {1} for the 'instant' type") {
                    genericCostFor(game) shouldBe 2
                }
            }

            test("non-instant/sorcery cards in the graveyard do not reduce cost") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Cryptic Serpent")
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .build()

                withClue("creature cards provide no discount") {
                    genericCostFor(game) shouldBe 5
                }
            }

            test("opponent's instants and sorceries do not reduce cost") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Cryptic Serpent")
                    .withCardInGraveyard(2, "Shock")
                    .withCardInGraveyard(2, "Divination")
                    .build()

                withClue("only your own graveyard counts") {
                    genericCostFor(game) shouldBe 5
                }
            }

            test("reduction floors at 0 generic — an overshooting discount never eats the pips") {
                val builder = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Cryptic Serpent")
                repeat(8) { builder.withCardInGraveyard(1, "Shock") }
                val game = builder.build()

                withClue("generic cost floors at 0 even when the discount overshoots") {
                    genericCostFor(game) shouldBe 0
                }
            }
        }
    }
}
