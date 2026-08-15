package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Discerning Peddler (OTJ #121) — {1}{R} Human Rogue 2/2.
 *
 * "When this creature enters, you may discard a card. If you do, draw a card."
 *
 * The ETB is a MayEffect → IfYouDoEffect(Discard, DrawCards). Verifies the accept path (a card is
 * discarded and a card drawn) and the decline path (hand and graveyard unchanged).
 */
class DiscerningPeddlerScenarioTest : ScenarioTestBase() {

    init {
        context("Discerning Peddler ETB loot") {

            test("entering loots: discard a card then draw a card") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Discerning Peddler")
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Discerning Peddler")
                withClue("casting Discerning Peddler should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                // ETB MayEffect: accept, then choose the card to discard. With a single
                // discardable card the engine auto-selects it (no second decision), so guard
                // the explicit selection like the Rescue Leopard loot test.
                if (game.hasPendingDecision()) {
                    game.answerYesNo(true)
                    game.resolveStack()
                }
                if (game.hasPendingDecision()) {
                    val grizzly = game.state.getHand(game.player1Id).first { id ->
                        game.state.getEntity(id)?.get<CardComponent>()?.name == "Grizzly Bears"
                    }
                    game.selectCards(listOf(grizzly))
                    game.resolveStack()
                }

                withClue("Grizzly Bears should have been discarded to the graveyard") {
                    game.findCardsInGraveyard(1, "Grizzly Bears").size shouldBe 1
                }
                withClue("the drawn Forest should be in hand") {
                    game.state.getHand(game.player1Id).count { id ->
                        game.state.getEntity(id)?.get<CardComponent>()?.name == "Forest"
                    } shouldBe 1
                }
            }

            test("declining leaves hand and graveyard unchanged") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Discerning Peddler")
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Discerning Peddler").error shouldBe null
                game.resolveStack()

                if (game.hasPendingDecision()) {
                    game.answerYesNo(false)
                    game.resolveStack()
                }

                withClue("nothing discarded when declining") {
                    game.findCardsInGraveyard(1, "Grizzly Bears").size shouldBe 0
                }
                withClue("Grizzly Bears still in hand, Forest not drawn") {
                    game.state.getHand(game.player1Id).count { id ->
                        game.state.getEntity(id)?.get<CardComponent>()?.name == "Forest"
                    } shouldBe 0
                }
            }
        }
    }
}
