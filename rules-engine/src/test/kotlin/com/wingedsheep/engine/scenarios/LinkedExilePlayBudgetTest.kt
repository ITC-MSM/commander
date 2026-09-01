package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.LinkedExileComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantMayCastFromLinkedExile
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * What a [GrantMayCastFromLinkedExile] does about *lands* in its pile.
 *
 * Two propositions, both about keeping the land-play path and the cast path answering the same
 * question:
 *
 *  - **`oncePerTurn` is one allowance for the permanent, not one per kind of play.** A land played
 *    out of the pile spends the same marker a cast spends, which is what "you may play a land **or**
 *    cast a spell" (Hauken's Insight) requires.
 *  - **The grant's filter decides which lands are playable, judged against the land itself.**
 *    `GameObjectFilter.Nonland` and `GameObjectFilter.Creature` both exclude a land card, but only
 *    the first carries an `IsNonland` predicate — so a filter's *shape* is not a usable proxy for
 *    "does this admit lands".
 */
class LinkedExilePlayBudgetTest : ScenarioTestBase() {

    /** "Once during each of your turns, you may play a card exiled with this permanent for free." */
    private val onceKeeper = card("Once Keeper") {
        manaCost = "{2}"
        typeLine = "Artifact"
        staticAbility {
            ability = GrantMayCastFromLinkedExile(
                filter = GameObjectFilter.Any,
                duringYourTurnOnly = true,
                withoutPayingManaCost = true,
                oncePerTurn = true,
            )
        }
    }

    /** The same grant, but only creature cards — a land in its pile must stay unplayable. */
    private val creatureKeeper = card("Creature Keeper") {
        manaCost = "{2}"
        typeLine = "Artifact"
        staticAbility {
            ability = GrantMayCastFromLinkedExile(
                filter = GameObjectFilter.Creature,
                duringYourTurnOnly = true,
                withoutPayingManaCost = true,
            )
        }
    }

    init {
        cardRegistry.register(listOf(onceKeeper, creatureKeeper))

        fun exileOf(game: TestGame): List<EntityId> =
            game.state.getZone(ZoneKey(game.player1Id, Zone.EXILE)).toList()

        fun nameOf(game: TestGame, id: EntityId): String? =
            game.state.getEntity(id)?.get<CardComponent>()?.name

        fun exiled(game: TestGame, cardName: String): EntityId =
            exileOf(game).single { nameOf(game, it) == cardName }

        fun hasLandPlay(game: TestGame, cardName: String): Boolean =
            game.getLegalActions(1).any {
                it.actionType == "PlayLand" && it.description.contains(cardName)
            }

        fun hasCast(game: TestGame, cardName: String): Boolean =
            game.getLegalActions(1).any {
                it.actionType == "CastSpell" && it.description.contains(cardName)
            }

        /** [keeper] on the battlefield with [pile] exiled and linked to it. */
        fun keeperWithPile(keeper: String, vararg pile: String): TestGame {
            var builder = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, keeper)
                .withCardInLibrary(1, "Island")
            pile.forEach { builder = builder.withCardInExile(1, it) }
            val game = builder
                .withActivePlayer(1)
                .withPriorityPlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val keeperId = game.findPermanent(keeper)!!
            val exiledIds = exileOf(game)
            game.state = game.state.updateEntity(keeperId) { container ->
                container.with(LinkedExileComponent(exiledIds))
            }
            return game
        }

        test("a land play and a cast are both offered while the once-per-turn allowance is unspent") {
            val game = keeperWithPile("Once Keeper", "Forest", "Grizzly Bears")
            hasLandPlay(game, "Forest") shouldBe true
            hasCast(game, "Grizzly Bears") shouldBe true
        }

        test("playing a land from the pile closes the once-per-turn allowance for casts") {
            val game = keeperWithPile("Once Keeper", "Forest", "Grizzly Bears")

            val played = game.execute(PlayLand(game.player1Id, exiled(game, "Forest")))
            withClue("the land play should succeed: ${played.error}") { played.error shouldBe null }

            withClue("the allowance belongs to the permanent, not to a kind of play") {
                hasCast(game, "Grizzly Bears") shouldBe false
            }
        }

        test("casting from the pile closes the once-per-turn allowance for land plays") {
            val game = keeperWithPile("Once Keeper", "Forest", "Grizzly Bears")

            val cast = game.execute(
                CastSpell(game.player1Id, exiled(game, "Grizzly Bears"), emptyList())
            )
            withClue("the free cast should succeed: ${cast.error}") { cast.error shouldBe null }
            game.resolveStack()

            withClue("the same marker the cast set closes the land path too") {
                hasLandPlay(game, "Forest") shouldBe false
            }
        }

        test("a creature-only grant offers no land play for a land in its pile") {
            val game = keeperWithPile("Creature Keeper", "Forest", "Grizzly Bears")

            withClue("the grant still admits the creature it names") {
                hasCast(game, "Grizzly Bears") shouldBe true
            }
            withClue("a land does not match GameObjectFilter.Creature, IsNonland predicate or not") {
                hasLandPlay(game, "Forest") shouldBe false
            }
            withClue("and the handler agrees with the offer") {
                game.execute(PlayLand(game.player1Id, exiled(game, "Forest"))).error shouldBe
                    "Land is not in your hand"
            }
        }
    }
}
