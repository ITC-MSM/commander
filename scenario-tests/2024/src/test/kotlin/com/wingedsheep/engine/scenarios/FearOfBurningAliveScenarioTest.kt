package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.FearOfBurningAlive
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Fear of Burning Alive (DSK #135) — {4}{R}{R} Enchantment Creature — Nightmare 4/4.
 *
 *   "When this creature enters, it deals 4 damage to each opponent.
 *    Delirium — Whenever a source you control deals noncombat damage to an opponent, if there are
 *    four or more card types among cards in your graveyard, this creature deals that amount of
 *    damage to target creature that player controls."
 *
 * Verifies: the ETB deals 4 to the opponent; with delirium active, the delirium ability re-fires
 * (the ETB damage is itself "a source you control deals noncombat damage to an opponent") and deals
 * 4 to a creature that opponent controls. Without delirium, the ability does not re-deal.
 */
class FearOfBurningAliveScenarioTest : FunSpec({

    // Four distinct card types in the graveyard: creature, instant, sorcery, enchantment.
    val deliriumGraveyard = listOf("Grizzly Bears", "Lightning Bolt", "Goad Spell", "Test Enchantment")

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + FearOfBurningAlive)
        initMirrorMatch(deck = Deck.of("Mountain" to 60), startingLife = 20)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    test("ETB deals 4 to each opponent; with delirium, redeals 4 to a creature that opponent controls") {
        val d = driver()
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)
        deliriumGraveyard.forEach { d.putCardInGraveyard(me, it) }
        val oppBears = d.putCreatureOnBattlefield(opp, "Grizzly Bears") // 2/2

        val fear = d.putCardInHand(me, "Fear of Burning Alive")
        d.giveMana(me, Color.RED, 6)
        d.castSpell(me, fear).error shouldBe null
        // Resolve the spell. The ETB damage triggers the Delirium ability, which pauses for its target.
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        // Opponent took 4 from the ETB.
        d.getLifeTotal(opp) shouldBe 16

        println("DBGB2 gyTypes=" + d.getGraveyardCardNames(me) + " fearOnBf=" + d.getPermanents(me).map{d.getCardName(it)} + " oppBearsAlive=" + (d.state.getEntity(oppBears)!=null) + " pending=" + d.pendingDecision?.javaClass?.simpleName)
        // The Delirium ability is on the stack, asking for a target creature that opponent controls.
        (d.pendingDecision as ChooseTargetsDecision)
        d.submitTargetSelection(me, listOf(oppBears))
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()
        // The 2/2 took 4 noncombat damage from Fear of Burning Alive -> dead, in the graveyard.
        d.getGraveyard(opp).contains(oppBears) shouldBe true
    }

    test("without delirium the ETB still deals 4 but the delirium ability does not re-deal") {
        val d = driver()
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)
        // Only one card type in graveyard — no delirium.
        d.putCardInGraveyard(me, "Grizzly Bears")
        val oppBears = d.putCreatureOnBattlefield(opp, "Grizzly Bears")

        val fear = d.putCardInHand(me, "Fear of Burning Alive")
        d.giveMana(me, Color.RED, 6)
        d.castSpell(me, fear).error shouldBe null
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        // ETB hit the opponent for 4, and with no delirium the ability never asks for a target.
        d.getLifeTotal(opp) shouldBe 16
        (d.pendingDecision is ChooseTargetsDecision) shouldBe false
        d.getGraveyard(opp).contains(oppBears) shouldBe false
    }
})
