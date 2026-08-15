package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.CastSpellRecord
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Canyon Crab's end-step loot trigger.
 *
 * Oracle: "At the beginning of your end step, if you haven't cast a spell from your hand this
 * turn, draw a card, then discard a card."
 *
 * The trigger is an intervening-if (CR 603.4) gated on
 * `Not(YouCastSpellsThisTurn(1, fromZone = HAND))`, so:
 *  1. No spell cast from hand this turn → loots (library -1, graveyard +1).
 *  2. A spell cast from hand this turn → doesn't trigger at all.
 */
class CanyonCrabScenarioTest : ScenarioTestBase() {

    init {
        context("Canyon Crab end-step loot") {

            test("loots when no spell was cast from hand this turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Canyon Crab")
                    .withCardInHand(1, "Grizzly Bears") // a card to discard
                    .withCardInLibrary(1, "Grizzly Bears") // a card to draw
                    .withActivePlayer(1)
                    .build()

                val libBefore = game.state.getLibrary(game.player1Id).size
                val gyBefore = game.state.getGraveyard(game.player1Id).size

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("Canyon Crab should draw a card (library -1)") {
                    game.state.getLibrary(game.player1Id).size shouldBe libBefore - 1
                }

                // The loot's discard step presents a card-selection decision; choose one to discard.
                val discard = game.getPendingDecision()
                withClue("loot presents a discard selection over the hand") {
                    (discard is SelectCardsDecision) shouldBe true
                }
                game.selectCards(listOf((discard as SelectCardsDecision).options.first())).error shouldBe null
                game.resolveStack()

                withClue("Canyon Crab should discard a card (graveyard +1)") {
                    game.state.getGraveyard(game.player1Id).size shouldBe gyBefore + 1
                }
            }

            test("does not trigger when a spell was cast from hand this turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Canyon Crab")
                    .withCardInHand(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .build()

                // Record that a spell was cast from hand this turn — satisfies
                // YouCastSpellsThisTurn(1, fromZone = HAND), so the intervening-if is false.
                game.state = game.state.copy(
                    spellsCastThisTurnByPlayer = mapOf(
                        game.player1Id to listOf(
                            CastSpellRecord(
                                typeLine = TypeLine.parse("Instant"),
                                manaValue = 1,
                                colors = emptySet(),
                                isFaceDown = false,
                                castFromZone = Zone.HAND,
                            )
                        )
                    )
                )

                val libBefore = game.state.getLibrary(game.player1Id).size
                val gyBefore = game.state.getGraveyard(game.player1Id).size

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("library unchanged — no draw because a spell was cast from hand") {
                    game.state.getLibrary(game.player1Id).size shouldBe libBefore
                }
                withClue("graveyard unchanged — no discard from the loot trigger") {
                    game.state.getGraveyard(game.player1Id).size shouldBe gyBefore
                }
            }
        }
    }
}
