package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.FearOfInfinity
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Fear of Infinity (DSK #214) — {1}{U}{B} Enchantment Creature — Nightmare 2/2.
 *
 *   "Flying, lifelink. This creature can't block.
 *    Eerie — Whenever an enchantment you control enters and whenever you fully unlock a Room, you
 *    may return this card from your graveyard to your hand."
 *
 * Verifies the Eerie graveyard-recursion half: while Fear of Infinity is in your graveyard, an
 * enchantment you control entering offers an optional return to hand. Confirming the "may" returns
 * the card; declining leaves it in the graveyard.
 */
class FearOfInfinityScenarioTest : FunSpec({

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + FearOfInfinity)
        initMirrorMatch(deck = Deck.of("Plains" to 60), startingLife = 20)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    test("an enchantment you control entering returns Fear of Infinity from graveyard to hand") {
        val d = driver()
        val me = d.activePlayer!!
        val fear = d.putCardInGraveyard(me, "Fear of Infinity")

        // Cast an enchantment you control — its ETB fires the Eerie trigger from the graveyard.
        val ench = d.putCardInHand(me, "Test Enchantment") // {1}{W}
        d.giveMana(me, Color.WHITE, 2)
        d.castSpell(me, ench).error shouldBe null
        // Resolve the enchantment; its ETB fires the Eerie trigger, which pauses for the "may".
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        // Optional "you may return" — confirm yes.
        d.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        d.submitYesNo(me, true)
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        d.getHand(me) shouldContain fear
        d.getGraveyard(me) shouldNotContain fear
    }

    test("declining the Eerie may leaves Fear of Infinity in the graveyard") {
        val d = driver()
        val me = d.activePlayer!!
        val fear = d.putCardInGraveyard(me, "Fear of Infinity")

        val ench = d.putCardInHand(me, "Test Enchantment")
        d.giveMana(me, Color.WHITE, 2)
        d.castSpell(me, ench).error shouldBe null
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        d.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        d.submitYesNo(me, false)
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        d.getGraveyard(me) shouldContain fear
        d.getHand(me) shouldNotContain fear
    }
})
