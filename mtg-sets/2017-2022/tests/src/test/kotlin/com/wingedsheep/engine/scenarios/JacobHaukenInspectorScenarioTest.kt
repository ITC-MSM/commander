package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.LinkedExileComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.engine.view.Visibility
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Jacob Hauken, Inspector // Hauken's Insight (VOW #65).
 *
 *   Front — Jacob Hauken, Inspector (0/2) — {T}: Draw a card, then exile a card from your hand
 *           face down. You may look at that card for as long as it remains exiled. You may pay
 *           {4}{U}{U}. If you do, transform Jacob Hauken.
 *   Back  — Hauken's Insight — At the beginning of your upkeep, exile the top card of your library
 *           face down. You may look at that card for as long as it remains exiled. Once during
 *           each of your turns, you may play a land or cast a spell from among the cards exiled
 *           with this permanent without paying its mana cost.
 *
 * Three propositions carry the card, and each is something the engine could not express before:
 *
 *  1. **You can see your own face-down exiled cards; your opponent cannot.** CR 708.5 gives exile
 *     no controller baseline, so the look has to be granted explicitly — and it is *only* a look:
 *     the front face grants no permission to play what it exiled.
 *  2. **The pile survives the flip**, because the link rides the permanent and transforming does
 *     not create a new object (CR 712.9).
 *  3. **"Play a land **or** cast a spell" is one allowance, not two.** Whichever the controller
 *     takes, the other is gone for the turn.
 */
class JacobHaukenInspectorScenarioTest : ScenarioTestBase() {

    private val visibility: Visibility
        get() = Visibility(cardRegistry)

    init {
        /**
         * Take every offer the resolution makes — exile the first card, say yes to "Pay {4}{U}{U}?",
         * auto-tap for it — and resolve, until the game is quiet.
         *
         * The pay prompt only ever appears when the mana is actually there: `Gate.MayPay` checks
         * affordability before prompting, so the tests that seed no lands never see it and never
         * transform, which is what lets one drain serve both.
         */
        fun drain(game: TestGame) {
            var guard = 0
            while (guard++ < 20) {
                val decision = game.getPendingDecision()
                when {
                    decision is SelectCardsDecision ->
                        game.selectCards(listOf(decision.options.first()))
                    decision is YesNoDecision -> game.answerYesNo(true)
                    decision is SelectManaSourcesDecision -> game.submitManaSourcesAutoPay()
                    game.state.stack.isNotEmpty() -> game.resolveStack()
                    else -> return
                }
            }
        }

        fun exileOf(game: TestGame): List<EntityId> =
            game.state.getZone(ZoneKey(game.player1Id, Zone.EXILE)).toList()

        fun nameOf(game: TestGame, id: EntityId): String? =
            game.state.getEntity(id)?.get<CardComponent>()?.name

        fun exiled(game: TestGame, cardName: String): EntityId =
            exileOf(game).single { nameOf(game, it) == cardName }

        fun canSee(game: TestGame, cardId: EntityId, viewerNumber: Int): Boolean {
            val viewerId = if (viewerNumber == 1) game.player1Id else game.player2Id
            return visibility.isCardIdentityVisibleTo(
                game.state,
                ZoneKey(game.player1Id, Zone.EXILE),
                cardId,
                viewerId,
            )
        }

        fun hasLandPlay(game: TestGame, cardName: String): Boolean =
            game.getLegalActions(1).any {
                it.actionType == "PlayLand" && it.description.contains(cardName)
            }

        fun hasCast(game: TestGame, cardName: String): Boolean =
            game.getLegalActions(1).any {
                it.actionType == "CastSpell" && it.description.contains(cardName)
            }

        fun activateHauken(game: TestGame) {
            val hauken = game.findPermanent("Jacob Hauken, Inspector")!!
            val abilityId = cardRegistry.getCard("Jacob Hauken, Inspector")!!
                .activatedAbilities.first().id
            val result = game.execute(ActivateAbility(game.player1Id, hauken, abilityId))
            withClue("activating the {T} ability should succeed: ${result.error}") {
                result.error shouldBe null
            }
            drain(game)
        }

        context("Jacob Hauken, Inspector") {

            test("the tap ability draws, exiles a hand card face down, and only you may look at it") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Jacob Hauken, Inspector", summoningSickness = false)
                    .withCardInHand(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                activateHauken(game)

                withClue("exactly one card left hand for exile") { exileOf(game).size shouldBe 1 }
                val exiledId = exileOf(game).single()
                withClue("it is exiled face down") {
                    game.state.getEntity(exiledId)!!.has<FaceDownComponent>() shouldBe true
                }
                withClue("'You may look at that card' — its controller keeps seeing it") {
                    canSee(game, exiledId, viewerNumber = 1) shouldBe true
                }
                withClue("face down in exile stays hidden from anyone the grant does not name") {
                    canSee(game, exiledId, viewerNumber = 2) shouldBe false
                }
                withClue("the draw happens before the exile, so the hand is back to one card") {
                    game.handSize(1) shouldBe 1
                }
            }

            test("looking at the exiled card is not permission to play it") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Jacob Hauken, Inspector", summoningSickness = false)
                    .withCardInHand(1, "Forest")
                    .withCardInLibrary(1, "Island")
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                activateHauken(game)

                withClue("the front face exiled the Forest") {
                    exileOf(game).any { nameOf(game, it) == "Forest" } shouldBe true
                }
                withClue("the front face grants a look, never a play") {
                    hasLandPlay(game, "Forest") shouldBe false
                }
            }

            test("paying {4}{U}{U} during resolution transforms Jacob Hauken") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Jacob Hauken, Inspector", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Island", 6)
                    .withCardInHand(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                activateHauken(game)

                withClue("the {4}{U}{U} was affordable and paid, so the card is on its back face") {
                    game.isOnBattlefield("Hauken's Insight") shouldBe true
                    game.isOnBattlefield("Jacob Hauken, Inspector") shouldBe false
                }
            }
        }

        context("Hauken's Insight") {

            /**
             * Hauken's Insight on the battlefield with [pile] already exiled under it. Seeding the
             * `LinkedExileComponent` directly is the established scenario idiom for a linked pile
             * (see `CemeteryIlluminatorScenarioTest`): it is the state the front face's exile
             * produces, without spending three turns producing it.
             */
            fun insightWithPile(vararg pile: String): TestGame {
                var builder = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Hauken's Insight")
                    .withCardInLibrary(1, "Island")
                pile.forEach { builder = builder.withCardInExile(1, it) }
                val game = builder
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val insight = game.findPermanent("Hauken's Insight")!!
                val exiledIds = exileOf(game)
                game.state = game.state.updateEntity(insight) { container ->
                    container.with(LinkedExileComponent(exiledIds))
                }
                return game
            }

            test("both a land play and a free cast are offered from the pile") {
                val game = insightWithPile("Forest", "Grizzly Bears")
                withClue("'play a land or cast a spell' — before either is used, both are offered") {
                    hasLandPlay(game, "Forest") shouldBe true
                    hasCast(game, "Grizzly Bears") shouldBe true
                }
            }

            test("playing a land from the pile spends the allowance a cast would have used") {
                val game = insightWithPile("Forest", "Grizzly Bears")

                val played = game.execute(PlayLand(game.player1Id, exiled(game, "Forest")))
                withClue("playing the exiled Forest should succeed: ${played.error}") {
                    played.error shouldBe null
                }
                withClue("the Forest is on the battlefield") {
                    game.isOnBattlefield("Forest") shouldBe true
                }
                withClue("'a land OR a spell' — the cast is gone for the rest of the turn") {
                    hasCast(game, "Grizzly Bears") shouldBe false
                }
            }

            test("casting from the pile spends the allowance the land play would have used") {
                val game = insightWithPile("Forest", "Grizzly Bears")

                val cast = game.execute(
                    CastSpell(game.player1Id, exiled(game, "Grizzly Bears"), emptyList())
                )
                withClue("casting the exiled Grizzly Bears for free should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("one allowance covers either kind of play, so the land play is gone too") {
                    hasLandPlay(game, "Forest") shouldBe false
                }
            }

            test("the upkeep trigger exiles the top card face down and leaves you able to look") {
                var builder = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Hauken's Insight")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(2, "Forest")
                val game = builder
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.BEGINNING, Step.UNTAP)
                    .build()

                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                drain(game)

                val exiledId = exiled(game, "Grizzly Bears")
                withClue("the top card was exiled face down") {
                    game.state.getEntity(exiledId)!!.has<FaceDownComponent>() shouldBe true
                }
                withClue("its controller may look at it for as long as it remains exiled") {
                    canSee(game, exiledId, viewerNumber = 1) shouldBe true
                }
                withClue("the opponent may not") {
                    canSee(game, exiledId, viewerNumber = 2) shouldBe false
                }
            }
        }
    }
}
