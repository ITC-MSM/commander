package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.FearOfImmobility
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Fear of Immobility (DSK #10) — {4}{W} Enchantment Creature — Nightmare 4/4.
 *
 *   "When this creature enters, tap up to one target creature. If an opponent controls that
 *    creature, put a stun counter on it."
 *
 * Verifies: an opposing creature is tapped AND gets a stun counter; a creature you control is
 * tapped but gets NO stun counter (the stun is gated on opponent control).
 */
class FearOfImmobilityScenarioTest : FunSpec({

    fun GameTestDriver.stunCount(entityId: EntityId): Int =
        state.getEntity(entityId)?.get<CountersComponent>()?.getCount(CounterType.STUN) ?: 0

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + FearOfImmobility)
        initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    test("ETB taps an opposing creature and stuns it") {
        val d = driver()
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)
        val bears = d.putCreatureOnBattlefield(opp, "Grizzly Bears")

        val fear = d.putCardInHand(me, "Fear of Immobility")
        d.giveMana(me, com.wingedsheep.sdk.core.Color.WHITE, 5)
        d.castSpell(me, fear).error shouldBe null
        // Resolve the spell so the ETB trigger goes on the stack and pauses for its target.
        d.bothPass()

        // The ETB trigger asks for its (up to one) target.
        (d.pendingDecision as ChooseTargetsDecision)
        d.submitTargetSelection(me, listOf(bears))
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        d.isTapped(bears) shouldBe true
        d.stunCount(bears) shouldBe 1
    }

    test("ETB tapping your own creature gives no stun counter") {
        val d = driver()
        val me = d.activePlayer!!
        val myBears = d.putCreatureOnBattlefield(me, "Grizzly Bears")

        val fear = d.putCardInHand(me, "Fear of Immobility")
        d.giveMana(me, com.wingedsheep.sdk.core.Color.WHITE, 5)
        d.castSpell(me, fear).error shouldBe null
        d.bothPass()

        (d.pendingDecision as ChooseTargetsDecision)
        d.submitTargetSelection(me, listOf(myBears))
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        d.isTapped(myBears) shouldBe true
        d.stunCount(myBears) shouldBe 0
    }
})
