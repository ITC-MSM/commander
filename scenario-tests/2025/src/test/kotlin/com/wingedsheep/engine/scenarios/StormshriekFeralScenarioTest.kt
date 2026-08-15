package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Stormshriek Feral // Flush Out (TDM #124).
 *
 * Stormshriek Feral — {4}{R} Dragon, 3/3, Flying, haste; {1}{R}: +1/+0 until end of turn.
 * Flush Out — {1}{R} Sorcery — Omen: "Discard a card. If you do, draw two cards."
 *
 * Exercises the Omen face: the mandatory discard then a conditional draw two ([IfYouDoEffect]),
 * and the Omen-specific shuffle-back (the card returns to its owner's library on resolution, not
 * the graveyard).
 */
class StormshriekFeralScenarioTest : ScenarioTestBase() {

    init {
        context("Flush Out Omen face") {

            test("discard a card, draw two, then shuffle the Omen back into the library") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Stormshriek Feral")
                    .withCardInHand(1, "Mountain") // the card to discard
                    .withLandsOnBattlefield(1, "Mountain", 2) // {1}{R}
                    .withCardInLibrary(1, "Plains")
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Swamp")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cardId = game.state.getHand(game.player1Id).first {
                    game.state.getEntity(it)?.get<CardComponent>()?.name == "Stormshriek Feral"
                }
                val libraryBefore = game.librarySize(1) // 3

                // Cast the Omen face (faceIndex = 0).
                val cast = game.execute(
                    CastSpell(playerId = game.player1Id, cardId = cardId, faceIndex = 0)
                )
                withClue("Casting Flush Out should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                // Resolve: discard prompt may surface; resolveStack drives the mandatory discard.
                game.resolveStack()
                if (game.hasPendingDecision()) {
                    val mountain = game.findCardsInHand(1, "Mountain").first()
                    game.selectTargets(listOf(mountain))
                    game.resolveStack()
                }

                withClue("The Mountain was discarded to the graveyard") {
                    game.graveyardSize(1) shouldBe 1
                }
                // Net library: started at 3, drew 2, then Stormshriek Feral shuffles back in → 3 - 2 + 1 = 2.
                withClue("Drew two cards and shuffled the Omen back into the library") {
                    game.librarySize(1) shouldBe libraryBefore - 2 + 1
                }
                withClue("Flush Out resolved to the library, not the graveyard or battlefield") {
                    game.findCardsInLibrary(1, "Stormshriek Feral").size shouldBe 1
                    game.isOnBattlefield("Stormshriek Feral") shouldBe false
                }
            }
        }
    }
}
