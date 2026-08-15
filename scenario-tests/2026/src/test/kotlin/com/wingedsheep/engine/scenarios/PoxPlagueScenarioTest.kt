package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Pox Plague (Secrets of Strixhaven #94).
 *
 * Pox Plague ({B}{B}{B}{B}{B} Sorcery):
 *   Each player loses half their life, then discards half the cards in their hand, then sacrifices
 *   half the permanents they control of their choice. Round down each time.
 *
 * The three stages are processed separately and each "half" is rounded down (CR; original Pox
 * rulings). These tests pin the round-down life loss and verify the discard/sacrifice stages
 * reduce hand size and permanent count by the floor of half.
 */
class PoxPlagueScenarioTest : ScenarioTestBase() {

    init {
        context("Pox Plague — lose / discard / sacrifice half, rounded down") {

            test("each player loses half their life, rounded down") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Pox Plague")
                    .withLandsOnBattlefield(1, "Swamp", 5)
                    .withLifeTotal(1, 21)   // half rounded down = 10 -> 11
                    .withLifeTotal(2, 20)   // half rounded down = 10 -> 10
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Pox Plague").error shouldBe null
                game.resolveStack()
                // No cards in hand / no nonland choices beyond lands; auto-resolve any selections.
                repeat(8) {
                    if (game.hasPendingDecision()) {
                        // Pox's sacrifice stage may prompt; choose minimally via selectCards if needed.
                        runCatching { game.selectCards(emptyList()) }
                            .recoverCatching { game.skipTargets() }
                        game.resolveStack()
                    }
                }

                withClue("active player lost floor(21/2)=10 -> 11 life") {
                    game.getLifeTotal(1) shouldBe 11
                }
                withClue("opponent lost floor(20/2)=10 -> 10 life") {
                    game.getLifeTotal(2) shouldBe 10
                }
            }

            test("each player discards half their hand and sacrifices half their permanents, rounded down") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Pox Plague")
                    .withCardInHand(1, "Forest")
                    .withCardInHand(1, "Island")
                    .withCardInHand(1, "Mountain")          // after casting Pox: 3 cards -> discard floor(3/2)=1
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Hill Giant")
                    .withLandsOnBattlefield(1, "Swamp", 5)   // 5 swamps + 2 creatures = 7 perms -> sac floor(7/2)=3
                    .withLifeTotal(1, 20)
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Pox Plague").error shouldBe null
                val handAfterCast = game.handSize(1)   // 3 (Forest/Island/Mountain)
                val permsBefore = game.state.getBattlefield(game.player1Id).size
                game.resolveStack()

                // Walk the decision chain: discard then sacrifice selections. Each pending decision
                // names its player; satisfy it by picking the required number of that player's
                // eligible cards/permanents.
                var guard = 0
                while (game.hasPendingDecision() && guard++ < 20) {
                    val decision = game.getPendingDecision()
                    if (decision is com.wingedsheep.engine.core.SelectCardsDecision) {
                        // Pick exactly the maximum required (Pox's "half") from the offered options.
                        game.selectCards(decision.options.take(decision.maxSelections))
                    } else {
                        // Any other prompt (e.g. a may): take no action that changes counts.
                        runCatching { game.skipTargets() }
                    }
                    game.resolveStack()
                }

                withClue("active player discarded floor(3/2)=1 card (hand 3 -> 2)") {
                    game.handSize(1) shouldBe handAfterCast - 1
                }
                withClue("active player sacrificed floor(7/2)=3 permanents (7 -> 4)") {
                    game.state.getBattlefield(game.player1Id).size shouldBe permsBefore - 3
                }
            }
        }
    }
}
