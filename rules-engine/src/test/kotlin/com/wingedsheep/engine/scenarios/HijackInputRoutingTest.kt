package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.state.components.player.PlayerTurnHijackedComponent
import com.wingedsheep.engine.state.components.player.PlayerTurnHijackedComponent.HijackState
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.state.components.stack.SpellOnStackComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.engine.view.ClientStateTransformer
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import com.wingedsheep.engine.core.Outcome

/**
 * Phase 2B input-routing tests for the Mindslaver-style hijack mechanic.
 *
 * Per CR 722 / Mindslaver rulings, the affected player V remains the spell controller
 * and resource owner during a hijacked turn — V's mana pays, V's cards are cast, V is
 * the in-game actor. The controlling player H is just the input device. Action seats
 * always carry V's playerId; authorization happens at the server. The engine itself
 * needs no special-casing — `state.priorityPlayerId == V == action.playerId` keeps the
 * existing seat checks correct.
 *
 * These tests verify that property and the [ClientStateTransformer] hijack indicators.
 */
class HijackInputRoutingTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.initMirrorMatch(deck = Deck.of("Mountain" to 20, "Grizzly Bears" to 20), skipMulligans = true)
        return d
    }

    fun transformer(d: GameTestDriver): ClientStateTransformer =
        ClientStateTransformer(cardRegistry = d.cardRegistry)

    test("controller-driven cast: action tagged with V succeeds during ACTIVE hijack and resources come from V") {
        val d = driver()
        val active = d.activePlayer!!
        val opponent = d.getOpponent(active)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Hand V (the active player here) a Shock and the {R} to cast it. Then mark V's turn
        // ACTIVE-hijacked by the opponent. The opponent is the in-the-room controller; V is
        // the affected player whose resources will be spent.
        val shock = d.putCardInHand(active, "Shock")
        d.giveMana(active, Color.RED, 1)
        d.replaceState(
            d.state.updateEntity(active) { container ->
                container.with(
                    PlayerTurnHijackedComponent(controllerId = opponent, state = HijackState.ACTIVE)
                )
            }
        )

        // Sanity: the seam reports the opponent as the actor for V.
        d.state.actorFor(active) shouldBe opponent
        d.state.actorFor(opponent) shouldBe opponent

        // The action carries V's playerId — the convention is that whoever clicks, the
        // engine receives V as the actor. The server seat layer (tested separately) is
        // what validates the controller is allowed to submit on V's behalf.
        val result = d.submitSuccess(
            CastSpell(
                playerId = active,
                cardId = shock,
                targets = listOf(ChosenTarget.Player(opponent))
            )
        )
        result.outcome shouldBe Outcome.Done

        // Stack item is controlled by V (the affected player), not the controller.
        val stackId = d.state.stack.first()
        val spellComp = d.state.getEntity(stackId)?.get<SpellOnStackComponent>()
        spellComp shouldBe spellComp
        spellComp?.casterId shouldBe active

        // Mana came out of V's pool, not the controller's.
        d.state.getEntity(active)?.get<ManaPoolComponent>()?.total shouldBe 0
        d.state.getEntity(opponent)?.get<ManaPoolComponent>()?.total shouldBe 0

        // Card left V's hand (it's now on the stack).
        val vHand = d.state.getZone(ZoneKey(active, Zone.HAND))
        vHand shouldNotContain shock
    }

    test("ClientStateTransformer: youAreHijacking populated for the controller during ACTIVE hijack") {
        val d = driver()
        val active = d.activePlayer!!
        val opponent = d.getOpponent(active)

        d.replaceState(
            d.state.updateEntity(active) { container ->
                container.with(
                    PlayerTurnHijackedComponent(controllerId = opponent, state = HijackState.ACTIVE)
                )
            }
        )

        val controllerView = transformer(d).transform(d.state, viewingPlayerId = opponent)
        controllerView.youAreHijacking shouldBe active
        controllerView.youAreHijackedBy.shouldBeNull()
    }

    test("ClientStateTransformer: youAreHijackedBy populated for the affected player during ACTIVE hijack") {
        val d = driver()
        val active = d.activePlayer!!
        val opponent = d.getOpponent(active)

        d.replaceState(
            d.state.updateEntity(active) { container ->
                container.with(
                    PlayerTurnHijackedComponent(controllerId = opponent, state = HijackState.ACTIVE)
                )
            }
        )

        val affectedView = transformer(d).transform(d.state, viewingPlayerId = active)
        affectedView.youAreHijackedBy shouldBe opponent
        affectedView.youAreHijacking.shouldBeNull()
    }

    test("ClientStateTransformer: controller sees affected player's hand cards face-up during ACTIVE hijack") {
        val d = driver()
        val active = d.activePlayer!!
        val opponent = d.getOpponent(active)

        // Give V (the affected player) a card in hand so the controller has something to see.
        val shock = d.putCardInHand(active, "Shock")

        // Without hijack: controller sees no cards from V's hand (zone hidden).
        run {
            val pre = transformer(d).transform(d.state, viewingPlayerId = opponent)
            pre.cards.keys shouldNotContain shock
            val vHandZone = pre.zones.first { it.zoneId.ownerId == active && it.zoneId.zoneType == Zone.HAND }
            vHandZone.isVisible shouldBe false
        }

        // ACTIVE hijack: controller sees V's hand contents.
        d.replaceState(
            d.state.updateEntity(active) { container ->
                container.with(
                    PlayerTurnHijackedComponent(controllerId = opponent, state = HijackState.ACTIVE)
                )
            }
        )
        val controllerView = transformer(d).transform(d.state, viewingPlayerId = opponent)
        controllerView.cards.keys shouldContain shock
        val vHandZone = controllerView.zones.first { it.zoneId.ownerId == active && it.zoneId.zoneType == Zone.HAND }
        vHandZone.isVisible shouldBe true
        vHandZone.cardIds shouldContain shock

        // Spectator path must still mask the hand even when hijack is active.
        val spectatorView = transformer(d).transform(d.state, viewingPlayerId = opponent, isSpectator = true)
        spectatorView.cards.keys shouldNotContain shock
        val spectatorZone = spectatorView.zones.first { it.zoneId.ownerId == active && it.zoneId.zoneType == Zone.HAND }
        spectatorZone.isVisible shouldBe false
    }

    test("ClientStateTransformer: both fields null when no hijack and when only SCHEDULED") {
        val d = driver()
        val active = d.activePlayer!!
        val opponent = d.getOpponent(active)

        // No hijack at all — both viewers see null.
        val cleanController = transformer(d).transform(d.state, viewingPlayerId = opponent)
        cleanController.youAreHijacking.shouldBeNull()
        cleanController.youAreHijackedBy.shouldBeNull()
        val cleanAffected = transformer(d).transform(d.state, viewingPlayerId = active)
        cleanAffected.youAreHijacking.shouldBeNull()
        cleanAffected.youAreHijackedBy.shouldBeNull()

        // SCHEDULED hijack does not redirect input authority yet — fields stay null.
        d.replaceState(
            d.state.updateEntity(active) { container ->
                container.with(
                    PlayerTurnHijackedComponent(controllerId = opponent, state = HijackState.SCHEDULED)
                )
            }
        )
        val scheduledController = transformer(d).transform(d.state, viewingPlayerId = opponent)
        scheduledController.youAreHijacking.shouldBeNull()
        scheduledController.youAreHijackedBy.shouldBeNull()
        val scheduledAffected = transformer(d).transform(d.state, viewingPlayerId = active)
        scheduledAffected.youAreHijacking.shouldBeNull()
        scheduledAffected.youAreHijackedBy.shouldBeNull()
    }
})
