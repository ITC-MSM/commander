package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.OrderedResponse
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

/**
 * Footbottom Feast (LRW #115) — "Put any number of target creature cards from your graveyard on
 * top of your library. Draw a card."
 *
 * `unlimited = true` targeting scoped to your own graveyard. The cases that matter: a subset of
 * the creature cards can be chosen (not all of them), noncreature cards and an opponent's
 * graveyard are off limits, zero targets is legal because "any number" includes none, and the
 * draw is sequenced *after* the move so a single returned creature is the card you draw.
 */
class FootbottomFeastScenarioTest : ScenarioTestBase() {

    init {
        context("Footbottom Feast") {

            fun TestGame.graveyardCard(playerNumber: Int, name: String) =
                state.getGraveyard(if (playerNumber == 1) player1Id else player2Id)
                    .first { state.getEntity(it)?.get<CardComponent>()?.name == name }

            fun TestGame.handCard(name: String) = state.getHand(player1Id)
                .first { state.getEntity(it)?.get<CardComponent>()?.name == name }

            test("returns the chosen subset to the library top, then draws the top card") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Footbottom Feast")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withCardInGraveyard(1, "Hill Giant")
                    .withCardInGraveyard(1, "Shock") // noncreature control — must not be targetable
                    .withCardInLibrary(1, "Swamp")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.graveyardCard(1, "Grizzly Bears")
                val giant = game.graveyardCard(1, "Hill Giant")

                val cast = game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = game.handCard("Footbottom Feast"),
                        targets = listOf(
                            ChosenTarget.Card(bears, game.player1Id, Zone.GRAVEYARD),
                            ChosenTarget.Card(giant, game.player1Id, Zone.GRAVEYARD)
                        )
                    )
                )
                withClue("Two creature cards in your own graveyard are legal targets: ${cast.error}") {
                    cast.error shouldBe null
                }

                game.resolveStack()
                // Two cards on top means CR 401.4 hands you the order: Bears first, then Giant.
                game.state.pendingDecision?.let {
                    game.submitDecision(OrderedResponse(it.id, listOf(bears, giant)))
                }
                game.resolveStack()

                withClue("The Bears was on top, so the draw picked it up") {
                    game.state.getHand(game.player1Id).contains(bears) shouldBe true
                }
                withClue("The Giant is the new top card, above the pre-existing Swamp") {
                    game.state.getLibrary(game.player1Id).first() shouldBe giant
                }
                withClue("Shock was never targeted and stays in the graveyard") {
                    val shock = game.graveyardCard(1, "Shock")
                    game.state.getGraveyard(game.player1Id).contains(shock) shouldBe true
                }
            }

            test("a creature card in an opponent's graveyard is not a legal target") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Footbottom Feast")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardInGraveyard(2, "Grizzly Bears")
                    .withCardInLibrary(1, "Swamp")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val theirBears = game.graveyardCard(2, "Grizzly Bears")

                val cast = game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = game.handCard("Footbottom Feast"),
                        targets = listOf(ChosenTarget.Card(theirBears, game.player2Id, Zone.GRAVEYARD))
                    )
                )
                withClue("\"from your graveyard\" excludes the opponent's") {
                    (cast.error != null) shouldBe true
                }
            }

            test("zero targets is legal — the spell is still a cantrip") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Footbottom Feast")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardInLibrary(1, "Swamp")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.state.getHand(game.player1Id).size

                val cast = game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = game.handCard("Footbottom Feast"),
                        targets = emptyList()
                    )
                )
                withClue("\"Any number\" includes none, even with an empty graveyard: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("Feast left hand and one card was drawn, so the hand is back to its old size") {
                    game.state.getHand(game.player1Id).size shouldBe handBefore
                }
                withClue("The library card was the one drawn") {
                    game.state.getHand(game.player1Id).map {
                        game.state.getEntity(it)?.get<CardComponent>()?.name
                    } shouldContainExactly listOf("Swamp")
                }
            }
        }
    }
}
