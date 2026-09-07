package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.CaptivatingGlance
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Captivating Glance — "At the beginning of your end step, clash with an opponent. If you win, gain
 * control of enchanted creature. Otherwise, that player gains control of enchanted creature."
 *
 * The card is pure composition, but two of its claims are worth proving rather than assuming: that
 * the clash pattern's `otherwise` leg can still read the opponent the clash chose
 * (`Player.ChosenOpponent` is written to the Aura by the clash's own prefix, *inside* the gate's
 * action, and read back *outside* it), and that repeating the trigger toggles control instead of
 * stacking a new Layer.CONTROL effect each turn.
 */
class CaptivatingGlanceScenarioTest : FunSpec({

    // A five-drop, so putting it on top of a library wins the clash against a Plains.
    val boulder = card("Glance Boulder") { manaCost = "{5}"; typeLine = "Artifact"; oracleText = "" }

    fun GameTestDriver.attachAura(auraId: EntityId, hostId: EntityId) {
        replaceState(state.updateEntity(auraId) { it.with(AttachedToComponent(hostId)) })
    }

    fun driver() = GameTestDriver().apply {
        registerCards(TestCards.all + listOf(CaptivatingGlance, boulder))
        initMirrorMatch(Deck.of("Plains" to 40), startingPlayer = 0)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    /** Stack the libraries so player 1 wins (or loses) the clash, then resolve both reveals. */
    fun GameTestDriver.clashThroughEndStep(youWin: Boolean) {
        putCardOnTopOfLibrary(player1, if (youWin) "Glance Boulder" else "Plains")
        putCardOnTopOfLibrary(player2, if (youWin) "Plains" else "Glance Boulder")
        passPriorityUntil(Step.END)
        // The step trigger is on the stack but unresolved when the step first matches.
        bothPass().error shouldBe null
        repeat(2) {
            val decision = pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
            // Empty selection = leave the revealed card on top.
            submitCardSelection(decision.playerId, emptyList()).error shouldBe null
        }
        pendingDecision shouldBe null
    }

    test("winning the clash takes control of the enchanted creature") {
        val d = driver()
        val creature = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
        val aura = d.putPermanentOnBattlefield(d.player1, "Captivating Glance")
        d.attachAura(aura, creature)

        d.state.projectedState.getController(creature) shouldBe d.player2
        d.clashThroughEndStep(youWin = true)
        d.state.projectedState.getController(creature) shouldBe d.player1
        // "Captivating Glance's controller never changes as a result of this card."
        d.state.projectedState.getController(aura) shouldBe d.player1
    }

    test("losing the clash hands the enchanted creature to the opponent you clashed with") {
        val d = driver()
        // Start it under player 1's control so the losing branch is an observable change.
        val creature = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        val aura = d.putPermanentOnBattlefield(d.player1, "Captivating Glance")
        d.attachAura(aura, creature)

        d.clashThroughEndStep(youWin = false)
        d.state.projectedState.getController(creature) shouldBe d.player2
    }

    test("the control change survives the Aura leaving the battlefield") {
        val d = driver()
        val creature = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
        val aura = d.putPermanentOnBattlefield(d.player1, "Captivating Glance")
        d.attachAura(aura, creature)

        d.clashThroughEndStep(youWin = true)
        d.state.projectedState.getController(creature) shouldBe d.player1

        d.moveToGraveyard(aura)
        d.state.projectedState.getController(creature) shouldBe d.player1
    }

    test("a second clash toggles control back rather than stacking a second control effect") {
        val d = driver()
        // This test walks a full turn cycle, so empty both hands first — otherwise the intervening
        // draw steps push a player over the hand limit and the cleanup discard prompt is picked up
        // by the next clash's decision loop.
        (d.getHand(d.player1) + d.getHand(d.player2)).forEach { d.moveToGraveyard(it) }

        val creature = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
        val aura = d.putPermanentOnBattlefield(d.player1, "Captivating Glance")
        d.attachAura(aura, creature)

        d.clashThroughEndStep(youWin = true)
        d.state.projectedState.getController(creature) shouldBe d.player1

        // Player 2's turn, then back around to player 1's. `passPriorityUntil` stops the instant the
        // step matches, so the two mains need a step in between to advance rather than no-op. Player
        // 2's own end step is quiet — the trigger is "your end step", i.e. player 1's.
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.passPriorityUntil(Step.END)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.activePlayer shouldBe d.player1

        d.clashThroughEndStep(youWin = false)
        d.state.projectedState.getController(creature) shouldBe d.player2
    }
})
