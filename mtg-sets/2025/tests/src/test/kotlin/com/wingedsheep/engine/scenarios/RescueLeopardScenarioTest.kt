package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Rescue Leopard (TDM #116).
 *
 * "Whenever this creature becomes tapped, you may discard a card. If you do, draw a card."
 *
 * Attacking taps the Leopard, firing the becomes-tapped loot trigger. Verifies the optional
 * discard-then-draw (a card is discarded and a card drawn) and the decline path (no change).
 */
class RescueLeopardScenarioTest : ScenarioTestBase() {

    init {
        context("Rescue Leopard") {

            test("tapping to attack loots: discard a card then draw a card") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Rescue Leopard", tapped = false, summoningSickness = false)
                    .withCardInHand(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                game.declareAttackers(mapOf("Rescue Leopard" to 2))
                game.resolveStack()

                // The trigger is a MayEffect: answer yes, then choose the card to discard.
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
                withClue("The drawn Forest should be in hand") {
                    game.state.getHand(game.player1Id).count { id ->
                        game.state.getEntity(id)?.get<CardComponent>()?.name == "Forest"
                    } shouldBe 1
                }
            }

            test("declining the loot leaves hand and graveyard unchanged") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Rescue Leopard", tapped = false, summoningSickness = false)
                    .withCardInHand(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                game.declareAttackers(mapOf("Rescue Leopard" to 2))
                game.resolveStack()

                if (game.hasPendingDecision()) {
                    game.answerYesNo(false)
                    game.resolveStack()
                }

                withClue("Nothing discarded when declining") {
                    game.findCardsInGraveyard(1, "Grizzly Bears").size shouldBe 0
                }
                withClue("Hand still holds just Grizzly Bears (no draw)") {
                    game.handSize(1) shouldBe 1
                }
            }
        }
    }
}
