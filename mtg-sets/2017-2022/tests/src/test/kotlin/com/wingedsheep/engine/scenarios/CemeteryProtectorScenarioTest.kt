package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.state.components.battlefield.LinkedExileComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Cemetery Protector (VOW #6) — {2}{W}{W} Creature — Human Soldier, 3/4.
 *
 * "Flash
 *  When this creature enters, exile a card from a graveyard.
 *  Whenever you play a land or cast a spell, if it shares a card type with the exiled card, create
 *  a 1/1 white Human creature token."
 *
 * The Protector is the *you*-scoped half of the cemetery cycle, which makes it the test that
 * separates the two scopes: an opponent's land drop must **not** make a Human. That asymmetry is
 * carried entirely by `EventPattern.LandPlayedEvent.player`, so a matcher that ignored the field
 * (or a card that reached for the any-player facade) would pass every other assertion here.
 */
class CemeteryProtectorScenarioTest : ScenarioTestBase() {

    init {
        context("Cemetery Protector") {

            /**
             * Seed the Protector on player 1's battlefield with [exiledCardName] already exiled and
             * linked to it — the state its own ETB produces, without paying four mana to get there.
             */
            fun protectorWithExile(exiledCardName: String, activePlayer: Int): TestGame {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Cemetery Protector")
                    .withCardInExile(2, exiledCardName)
                    .withCardInHand(1, "Forest")
                    .withCardInHand(2, "Forest")
                    .withCardInLibrary(1, "Plains")
                    .withCardInLibrary(2, "Plains")
                    .withActivePlayer(activePlayer)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val protector = game.findPermanent("Cemetery Protector")!!
                val exiled: EntityId = game.state.getExile(game.player2Id).first()
                game.state = game.state.updateEntity(protector) { container ->
                    container.with(LinkedExileComponent(listOf(exiled)))
                }
                return game
            }

            test("the ETB exiles a card from a graveyard and links it to the Protector") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Cemetery Protector")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withCardInGraveyard(2, "Grizzly Bears")
                    .withCardInLibrary(1, "Plains")
                    .withCardInLibrary(2, "Plains")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findCardsInGraveyard(2, "Grizzly Bears").first()

                game.castSpell(1, "Cemetery Protector").error shouldBe null
                if (game.getPendingDecision() is com.wingedsheep.engine.core.SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()
                // With a single card in any graveyard the engine auto-resolves the selection
                // rather than pausing on it; answer it only when it actually pauses.
                if (game.hasPendingDecision()) {
                    game.selectCards(listOf(bears)).error shouldBe null
                    game.resolveStack()
                }

                val protector = game.findPermanent("Cemetery Protector")!!
                withClue("the card left the opponent's graveyard for exile") {
                    game.isInExile(2, "Grizzly Bears") shouldBe true
                }
                withClue("and the exile is linked, so 'the exiled card' resolves later") {
                    game.state.getEntity(protector)?.get<LinkedExileComponent>()?.exiledIds shouldBe
                        listOf(bears)
                }
            }

            test("your land drop that shares a card type makes a Human") {
                val game = protectorWithExile("Mountain", activePlayer = 1)
                val forest = game.findCardsInHand(1, "Forest").first()

                game.execute(PlayLand(game.player1Id, forest)).error shouldBe null
                game.resolveStack()

                withClue("Forest and the exiled Mountain are both land cards") {
                    game.findAllPermanents("Human Token").size shouldBe 1
                }
            }

            test("your land drop that shares nothing makes no Human") {
                val game = protectorWithExile("Grizzly Bears", activePlayer = 1)
                val forest = game.findCardsInHand(1, "Forest").first()

                game.execute(PlayLand(game.player1Id, forest)).error shouldBe null
                game.resolveStack()

                withClue("a land card shares no card type with an exiled creature card") {
                    game.findAllPermanents("Human Token").size shouldBe 0
                }
            }

            test("an opponent's land drop makes nothing — this ability is you-scoped") {
                val game = protectorWithExile("Mountain", activePlayer = 2)
                val forest = game.findCardsInHand(2, "Forest").first()

                game.execute(PlayLand(game.player2Id, forest)).error shouldBe null
                game.resolveStack()

                withClue("\"whenever YOU play a land\" — the opponent's drop is not yours") {
                    game.findAllPermanents("Human Token").size shouldBe 0
                }
            }
        }
    }
}
