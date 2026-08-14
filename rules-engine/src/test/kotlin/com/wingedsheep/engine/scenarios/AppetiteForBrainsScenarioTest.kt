package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Appetite for Brains (AVR #84): target opponent reveals hand; exile a card with MV ≥ 4.
 */
class AppetiteForBrainsScenarioTest : ScenarioTestBase() {

    init {
        context("reveal hand and exile a mana-value-4+ card") {

            test("exiles the chosen high-MV card and leaves cheaper cards") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Appetite for Brains")
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withCardInHand(2, "Hill Giant") // MV 4 — legal
                    .withCardInHand(2, "Glory Seeker") // MV 2 — not legal
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cardId = game.state.getHand(game.player1Id).first {
                    game.state.getEntity(it)?.get<CardComponent>()?.name == "Appetite for Brains"
                }

                val cast = game.execute(
                    CastSpell(
                        game.player1Id,
                        cardId,
                        listOf(ChosenTarget.Player(game.player2Id)),
                    ),
                )
                withClue("Casting Appetite for Brains should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                if (game.hasPendingDecision()) {
                    val giant = game.state.getHand(game.player2Id).first {
                        game.state.getEntity(it)?.get<CardComponent>()?.name == "Hill Giant"
                    }
                    game.selectCards(listOf(giant))
                    game.resolveStack()
                }

                withClue("Hill Giant is exiled") {
                    game.state.getExile(game.player2Id).mapNotNull {
                        game.state.getEntity(it)?.get<CardComponent>()?.name
                    } shouldBe listOf("Hill Giant")
                }
                withClue("Glory Seeker stays in hand") {
                    game.state.getZone(game.player2Id, Zone.HAND).mapNotNull {
                        game.state.getEntity(it)?.get<CardComponent>()?.name
                    } shouldBe listOf("Glory Seeker")
                }
            }
        }
    }
}
