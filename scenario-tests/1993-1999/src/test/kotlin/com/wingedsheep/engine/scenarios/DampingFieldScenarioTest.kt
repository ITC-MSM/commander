package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Damping Field — "Players can't untap more than one artifact during their untap steps."
 *
 * Models the global untap-count cap (UntapLimitPerStep). With more than one tapped artifact that
 * would untap, the player must keep the excess tapped; exactly one untaps (their choice).
 */
class DampingFieldScenarioTest : FunSpec({

    val Widget = CardDefinition.artifact(name = "Test Widget", manaCost = ManaCost.parse("{1}"))

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(Widget))
        return d
    }

    test("with three tapped artifacts, only one untaps; the rest stay tapped") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val me = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putPermanentOnBattlefield(me, "Damping Field")
        val a1 = d.putPermanentOnBattlefield(me, "Test Widget")
        val a2 = d.putPermanentOnBattlefield(me, "Test Widget")
        val a3 = d.putPermanentOnBattlefield(me, "Test Widget")
        listOf(a1, a2, a3).forEach { d.tapPermanent(it) }

        // Advance to our next untap step. The cap forces a keep-tapped decision.
        d.passPriorityUntil(Step.UNTAP)
        val decision = d.pendingDecision
        (decision is SelectCardsDecision) shouldBe true
        decision as SelectCardsDecision
        // Three artifacts would untap; at most one may — so at least two must be kept tapped.
        decision.minSelections shouldBe 2

        // Keep a1 and a2 tapped; a3 untaps.
        d.submitCardSelection(me, listOf(a1, a2))
        d.state.getEntity(a1)?.has<TappedComponent>() shouldBe true
        d.state.getEntity(a2)?.has<TappedComponent>() shouldBe true
        d.state.getEntity(a3)?.has<TappedComponent>() shouldBe false
    }

    test("a single tapped artifact untaps freely — the cap is inert") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val me = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putPermanentOnBattlefield(me, "Damping Field")
        val a1 = d.putPermanentOnBattlefield(me, "Test Widget")
        d.tapPermanent(a1)

        // Only one artifact — no decision; it untaps on our next untap step (turn 2).
        d.passPriorityUntil(Step.UPKEEP)        // opponent's upkeep
        d.passPriorityUntil(Step.PRECOMBAT_MAIN) // opponent's main
        d.passPriorityUntil(Step.UPKEEP)        // our next upkeep — untap already happened
        d.activePlayer shouldBe me
        d.state.getEntity(a1)?.has<TappedComponent>() shouldBe false
    }
})
