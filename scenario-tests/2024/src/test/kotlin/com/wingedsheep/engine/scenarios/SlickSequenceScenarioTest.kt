package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Slick Sequence (OTJ #233) — {U}{R} Instant.
 *
 * "Slick Sequence deals 2 damage to any target. If you've cast another spell this turn, draw a card."
 *
 * The conditional draw is gated on `YouCastSpellsThisTurn(atLeast = 2)`, evaluated at resolution
 * when Slick Sequence is itself already recorded — so "another spell" means this spell plus at least
 * one other. Verifies: damage always lands; draw only when a prior spell was cast this turn.
 */
class SlickSequenceScenarioTest : ScenarioTestBase() {

    init {
        context("Slick Sequence") {

            test("no draw when it's the only spell cast this turn (damage still lands)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Slick Sequence")
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)
                val cast = game.castSpellTargetingPlayer(1, "Slick Sequence", 2)
                withClue("casting Slick Sequence should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("2 damage to the opponent (20 -> 18)") {
                    game.getLifeTotal(2) shouldBe 18
                }
                withClue("no card drawn — Slick Sequence is the only spell this turn") {
                    game.handSize(1) shouldBe handBefore - 1 // only the Slick Sequence left hand
                }
            }

            test("draws a card when another spell was cast this turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Lightning Bolt")
                    .withCardInHand(1, "Slick Sequence")
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // First spell this turn: Lightning Bolt at the opponent.
                game.castSpellTargetingPlayer(1, "Lightning Bolt", 2).error shouldBe null
                game.resolveStack()

                val handBefore = game.handSize(1) // Slick Sequence still in hand

                // Second spell: Slick Sequence. "Another spell" was cast, so it should draw.
                game.castSpellTargetingPlayer(1, "Slick Sequence", 2).error shouldBe null
                game.resolveStack()

                withClue("opponent took 3 (bolt) + 2 (sequence) = 5 damage (20 -> 15)") {
                    game.getLifeTotal(2) shouldBe 15
                }
                withClue("a card was drawn: Slick Sequence left hand (-1) but the Forest was drawn (+1) → net unchanged") {
                    game.handSize(1) shouldBe handBefore
                }
                withClue("the drawn Forest is in hand") {
                    game.findCardsInHand(1, "Forest").size shouldBe 1
                }
            }
        }
    }
}
