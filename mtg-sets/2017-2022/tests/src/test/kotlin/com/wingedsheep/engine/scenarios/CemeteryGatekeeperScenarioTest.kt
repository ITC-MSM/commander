package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.battlefield.LinkedExileComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Cemetery Gatekeeper (VOW #148) — {1}{R} Creature — Vampire, 2/1.
 *
 * "First strike
 *  When this creature enters, exile a card from a graveyard.
 *  Whenever a player plays a land or casts a spell, if it shares a card type with the exiled card,
 *  this creature deals 2 damage to that player."
 *
 * Three pieces of new engine behaviour, each of which alone would leave the card looking
 * implemented and resolving wrong:
 *  - `EventPattern.LandPlayedEvent` gained a `player` axis. Before it had one the pattern was
 *    hard-wired to "you play a land", so an *opponent's* land drop — most of what this card does —
 *    never fired.
 *  - `TriggerContext.fromEvent` had no `LandPlayedEvent` branch, so a land-play trigger resolved
 *    with an empty context: "it" was null (the intervening-"if" read false for every land) and
 *    "that player" was null (the damage went nowhere).
 *  - The intervening-"if" is `SharesCardTypeWith(LinkedExiledCard())`, which has to fail *closed* —
 *    an empty imprint pile matches nothing rather than everything.
 */
class CemeteryGatekeeperScenarioTest : ScenarioTestBase() {

    init {
        context("Cemetery Gatekeeper") {

            /**
             * Cast the Gatekeeper from player 1's hand so the ETB really fires, exiling
             * [exiledCardName] out of player 2's graveyard and linking it to the Gatekeeper.
             */
            fun castGatekeeperExiling(exiledCardName: String): TestGame {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Cemetery Gatekeeper")
                    .withCardInHand(1, "Forest")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withCardInGraveyard(2, exiledCardName)
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(2, "Mountain")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val exiledCard = game.findCardsInGraveyard(2, exiledCardName).first()

                game.castSpell(1, "Cemetery Gatekeeper").error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()
                // Only one card is in any graveyard, so the engine auto-resolves the selection
                // rather than pausing on it. Answer it only when it actually pauses.
                if (game.hasPendingDecision()) {
                    game.selectCards(listOf(exiledCard)).error shouldBe null
                    game.resolveStack()
                }

                val gatekeeper = game.findPermanent("Cemetery Gatekeeper")!!
                withClue("the ETB exile is linked to the Gatekeeper, not a plain exile") {
                    game.state.getEntity(gatekeeper)?.get<LinkedExileComponent>()?.exiledIds shouldBe
                        listOf(exiledCard)
                }
                return game
            }

            test("a land you play that shares a card type with the exiled card deals you 2") {
                // "a player" includes the Gatekeeper's own controller — the card is symmetric.
                val game = castGatekeeperExiling("Mountain")
                val forest = game.findCardsInHand(1, "Forest").first()
                val lifeBefore = game.getLifeTotal(1)

                game.execute(PlayLand(game.player1Id, forest)).error shouldBe null
                game.resolveStack()

                withClue("Forest and the exiled Mountain are both land cards") {
                    game.getLifeTotal(1) shouldBe lifeBefore - 2
                }
            }

            test("a land you play that shares nothing with the exiled card does nothing") {
                // The intervening-"if" has to read the *played land's* types. A trigger that fired
                // unconditionally — the shape you get when the trigger context has no triggering
                // entity and the condition is skipped rather than failed — passes the test above
                // and fails this one.
                val game = castGatekeeperExiling("Grizzly Bears")
                val forest = game.findCardsInHand(1, "Forest").first()
                val lifeBefore = game.getLifeTotal(1)

                game.execute(PlayLand(game.player1Id, forest)).error shouldBe null
                game.resolveStack()

                withClue("a land card shares no card type with an exiled creature card") {
                    game.getLifeTotal(1) shouldBe lifeBefore
                }
            }

            test("an opponent's land play fires the trigger and damages that opponent") {
                // The `player = Player.Each` axis. With the old You-only pattern this land drop
                // never reached the matcher at all.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Cemetery Gatekeeper")
                    .withCardInExile(2, "Mountain")
                    .withCardInHand(2, "Forest")
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(2, "Mountain")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val gatekeeper = game.findPermanent("Cemetery Gatekeeper")!!
                val exiled: EntityId = game.state.getExile(game.player2Id).first()
                game.state = game.state.updateEntity(gatekeeper) { container ->
                    container.with(LinkedExileComponent(listOf(exiled)))
                }

                val forest = game.findCardsInHand(2, "Forest").first()
                val lifeBefore = game.getLifeTotal(2)

                game.execute(PlayLand(game.player2Id, forest)).error shouldBe null
                game.resolveStack()

                withClue("the damage goes to the player who played the land, not the controller") {
                    game.getLifeTotal(2) shouldBe lifeBefore - 2
                    game.getLifeTotal(1) shouldBe 20
                }
            }

            test("with nothing exiled the trigger fails closed") {
                // An unresolvable LinkedExiledCard must match nothing. Matching *everything* is the
                // failure mode to guard against: it would turn the Gatekeeper into an unconditional
                // 2-damage-per-land-drop engine the moment every graveyard is empty on ETB.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Cemetery Gatekeeper")
                    .withCardInHand(1, "Forest")
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(2, "Mountain")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val forest = game.findCardsInHand(1, "Forest").first()
                val lifeBefore = game.getLifeTotal(1)

                game.execute(PlayLand(game.player1Id, forest)).error shouldBe null
                game.resolveStack()

                withClue("no exiled card means no shared card type") {
                    game.getLifeTotal(1) shouldBe lifeBefore
                }
            }
        }
    }
}
