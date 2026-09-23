package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActionProcessor
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.mechanics.layers.Layer
import com.wingedsheep.engine.mechanics.layers.SerializableModification
import com.wingedsheep.engine.mechanics.layers.addFloatingEffect
import com.wingedsheep.engine.state.components.battlefield.LinkedExileComponent
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.engine.view.ClientStateTransformer
import com.wingedsheep.mtg.sets.definitions.dom.cards.DeepFreeze
import com.wingedsheep.mtg.sets.definitions.dom.cards.RonaDiscipleOfGix
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.Duration
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * The linked-exile cast permission (`GrantMayCastFromLinkedExile`) is read from projected state,
 * and the offer, the handler and the client view all agree about it.
 *
 * Rona, Disciple of Gix: "You may cast spells from among cards exiled with Rona." Every path used to
 * find the grant for itself from base `ControllerComponent` and the printed static abilities, so a
 * stolen Rona still granted to its old controller, and a Rona that had lost all its abilities
 * (Deep Freeze) still granted at all. Now the legality kernel answers for the enumerator,
 * `CastSpellHandler` and `ClientStateTransformer`, and the three can't drift apart.
 */
class LinkedExileCastPermissionProjectionTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + RonaDiscipleOfGix + DeepFreeze)
        d.initMirrorMatch(deck = Deck.of("Forest" to 20, "Island" to 20), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /** Puts Rona under [ronaOwner] with a Grizzly Bears (owned by player 1) exiled with it. */
    fun GameTestDriver.ronaWithExiledBears(ronaOwner: EntityId): Pair<EntityId, EntityId> {
        val rona = putPermanentOnBattlefield(ronaOwner, "Rona, Disciple of Gix")
        val bears = putCardInExile(player1, "Grizzly Bears")
        replaceState(state.updateEntity(rona) { it.with(LinkedExileComponent(listOf(bears))) })
        putPermanentOnBattlefield(player1, "Forest")
        putPermanentOnBattlefield(player1, "Forest")
        return rona to bears
    }

    fun GameTestDriver.offered(player: EntityId, card: EntityId): Boolean =
        legalActions(player).any { (it.action as? CastSpell)?.cardId == card }

    fun GameTestDriver.accepted(player: EntityId, card: EntityId): String? =
        ActionProcessor(cardRegistry).validate(state, CastSpell(player, card))

    /** Hands [rona] to [newController] for the turn through a layer-2 control effect. */
    fun GameTestDriver.giveControl(rona: EntityId, newController: EntityId) = replaceState(
        state.addFloatingEffect(
            layer = Layer.CONTROL,
            modification = SerializableModification.ChangeController(newController),
            affectedEntities = setOf(rona),
            duration = Duration.EndOfTurn,
            context = EffectContext(sourceId = rona, controllerId = newController),
        )
    )

    fun GameTestDriver.shownCastable(viewer: EntityId, card: EntityId): Boolean =
        ClientStateTransformer(cardRegistry).transform(state, viewer).cards[card]?.playableFromExile == true

    test("Rona's controller is offered the exiled card, may cast it, and sees it as castable") {
        val d = driver()
        val (_, bears) = d.ronaWithExiledBears(d.player1)

        d.offered(d.player1, bears) shouldBe true
        d.accepted(d.player1, bears) shouldBe null
        d.shownCastable(d.player1, bears) shouldBe true
    }

    test("a Rona that lost all abilities grants nothing — to the offer, the handler or the view") {
        val d = driver()
        val (rona, bears) = d.ronaWithExiledBears(d.player1)

        val deepFreeze = d.putCardInHand(d.player1, "Deep Freeze")
        d.giveMana(d.player1, Color.BLUE, 1)
        d.giveColorlessMana(d.player1, 2)
        d.castSpell(d.player1, deepFreeze, listOf(rona)).error shouldBe null
        d.bothPass()
        d.state.projectedState.hasLostAllAbilities(rona) shouldBe true

        withClue("the enumerator must not offer it") { d.offered(d.player1, bears) shouldBe false }
        withClue("the handler must refuse it") { d.accepted(d.player1, bears) shouldNotBe null }
        withClue("the view must not flag it") { d.shownCastable(d.player1, bears) shouldBe false }
    }

    test("the permission follows projected control, not the base controller") {
        val d = driver()
        val (rona, bears) = d.ronaWithExiledBears(d.player2)
        d.giveControl(rona, d.player1)

        withClue("the player who controls Rona now") {
            d.offered(d.player1, bears) shouldBe true
            d.accepted(d.player1, bears) shouldBe null
            d.shownCastable(d.player1, bears) shouldBe true
        }
        withClue("Rona's base controller, who no longer controls it") {
            d.shownCastable(d.player2, bears) shouldBe false
        }
    }

    // The mirror of the test above, with the old controller holding priority: player 1 owns Rona
    // and is the active player, but player 2 controls it now, so player 1 may no longer cast from
    // its pile on any path.
    test("a player whose Rona was taken no longer has the permission") {
        val d = driver()
        val (rona, bears) = d.ronaWithExiledBears(d.player1)
        d.giveControl(rona, d.player2)

        withClue("the enumerator must not offer it") { d.offered(d.player1, bears) shouldBe false }
        withClue("the handler must refuse it") { d.accepted(d.player1, bears) shouldNotBe null }
        withClue("the view must not flag it") { d.shownCastable(d.player1, bears) shouldBe false }
    }

    test("a face-down Rona grants nothing — to the offer, the handler or the view") {
        val d = driver()
        val (rona, bears) = d.ronaWithExiledBears(d.player1)
        d.replaceState(d.state.updateEntity(rona) { it.with(FaceDownComponent) })

        withClue("the enumerator must not offer it") { d.offered(d.player1, bears) shouldBe false }
        withClue("the handler must refuse it") { d.accepted(d.player1, bears) shouldNotBe null }
        withClue("the view must not flag it") { d.shownCastable(d.player1, bears) shouldBe false }
    }
})
