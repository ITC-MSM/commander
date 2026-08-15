package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Grab the Prize (DSK) — {1}{R} Sorcery.
 *
 * "As an additional cost to cast this spell, discard a card.
 *  Draw two cards. If the discarded card wasn't a land card, Grab the Prize deals 2 damage to
 *  each opponent."
 *
 * Exercises the new `EffectTarget.DiscardedAsCost` role via `Conditions.DiscardedCardMatches`:
 * the conditional damage gates on whether the discarded card (in the graveyard by resolution)
 * was a land.
 */
class GrabThePrizeScenarioTest : ScenarioTestBase() {

    init {
        context("Grab the Prize discard-gated damage") {

            test("discard a nonland card: draw two and deal 2 to each opponent") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Grab the Prize")
                    .withCardInHand(1, "Grizzly Bears") // nonland card to discard
                    .withLandsOnBattlefield(1, "Mountain", 2) // {1}{R}
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(1, "Mountain")
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val hand = game.state.getHand(game.player1Id)
                val spellId = hand.first {
                    game.state.getEntity(it)?.get<CardComponent>()?.name == "Grab the Prize"
                }
                val discardId = hand.first {
                    game.state.getEntity(it)?.get<CardComponent>()?.name == "Grizzly Bears"
                }

                val cast = game.execute(
                    CastSpell(
                        game.player1Id,
                        spellId,
                        emptyList(),
                        additionalCostPayment = AdditionalCostPayment(discardedCards = listOf(discardId))
                    )
                )
                withClue("Cast discarding a nonland should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                // Hand started: Grab the Prize + Grizzly Bears (2). Cast removes Grab, discard removes
                // Grizzly Bears → 0; then draw 2 → 2 cards in hand.
                withClue("Drew two cards") {
                    game.state.getHand(game.player1Id).size shouldBe 2
                }
                withClue("Nonland discard deals 2 to the opponent (20 -> 18)") {
                    game.state.lifeTotal(game.player2Id) shouldBe 18
                }
            }

            test("discard a land card: draw two, NO damage to opponent") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Grab the Prize")
                    .withCardInHand(1, "Mountain") // land card to discard
                    .withLandsOnBattlefield(1, "Mountain", 2) // {1}{R}
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val hand = game.state.getHand(game.player1Id)
                val spellId = hand.first {
                    game.state.getEntity(it)?.get<CardComponent>()?.name == "Grab the Prize"
                }
                val discardId = hand.first {
                    game.state.getEntity(it)?.get<CardComponent>()?.name == "Mountain"
                }

                val cast = game.execute(
                    CastSpell(
                        game.player1Id,
                        spellId,
                        emptyList(),
                        additionalCostPayment = AdditionalCostPayment(discardedCards = listOf(discardId))
                    )
                )
                withClue("Cast discarding a land should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("Drew two cards") {
                    game.state.getHand(game.player1Id).size shouldBe 2
                }
                withClue("Land discard deals no damage (opponent stays at 20)") {
                    game.state.lifeTotal(game.player2Id) shouldBe 20
                }
            }
        }
    }
}
