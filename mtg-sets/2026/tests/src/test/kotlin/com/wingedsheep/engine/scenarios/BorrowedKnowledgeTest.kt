package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.BorrowedKnowledge
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Tests for Borrowed Knowledge (SOS #178).
 *
 * {2}{R}{W} Sorcery — Choose one —
 * • Discard your hand, then draw cards equal to the number of cards in target opponent's hand.
 * • Discard your hand, then draw cards equal to the number of cards discarded this way.
 */
class BorrowedKnowledgeTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(BorrowedKnowledge))
        return driver
    }

    fun castAndChooseMode(driver: GameTestDriver, caster: EntityId, modePrefix: String): EntityId {
        driver.giveMana(caster, Color.RED, 1)
        driver.giveMana(caster, Color.WHITE, 1)
        driver.giveColorlessMana(caster, 2)
        val card = driver.putCardInHand(caster, "Borrowed Knowledge")
        driver.castSpell(caster, card)
        val modeDecision = driver.pendingDecision
        modeDecision.shouldBeInstanceOf<ChooseOptionDecision>()
        val idx = modeDecision.options.indexOfFirst { it.startsWith(modePrefix) }
        require(idx >= 0) { "Mode '$modePrefix' not offered; options=${modeDecision.options}" }
        driver.submitDecision(caster, OptionChosenResponse(modeDecision.id, idx))
        return card
    }

    test("mode 1 - discard your hand, draw equal to target opponent's hand size") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 30, "Mountain" to 30), startingLife = 20)
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Give me a few junk cards to discard, and give the opponent a known hand size.
        repeat(2) { driver.putCardInHand(me, "Island") }
        repeat(4) { driver.putCardInHand(opp, "Mountain") }

        val oppHand = driver.getHandSize(opp)

        castAndChooseMode(driver, me, "Discard your hand, then draw cards equal to the number of cards in target opponent")
        // Mode 1 targets an opponent.
        val targetDecision = driver.pendingDecision
        targetDecision.shouldBeInstanceOf<ChooseTargetsDecision>()
        driver.submitDecision(me, TargetsResponse(targetDecision.id, mapOf(0 to listOf(opp))))
        driver.bothPass()

        // Borrowed Knowledge is gone (it was cast), my whole prior hand was discarded, then I drew
        // a number of cards equal to the opponent's hand size at resolution.
        driver.getHandSize(me) shouldBe oppHand
    }

    test("mode 2 - discard your hand, draw equal to the number of cards discarded this way") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 30, "Mountain" to 30), startingLife = 20)
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Seed three extra junk cards on top of the opening hand.
        repeat(3) { driver.putCardInHand(me, "Island") }

        castAndChooseMode(driver, me, "Discard your hand, then draw cards equal to the number of cards discarded")
        // Borrowed Knowledge is now on the stack; the remaining hand is exactly what will be
        // discarded, and the same number is then redrawn — so discard-then-draw is net-neutral.
        val handToDiscard = driver.getHandSize(me)
        (handToDiscard > 0) shouldBe true // there is a non-empty hand to discard
        driver.bothPass()

        driver.getHandSize(me) shouldBe handToDiscard
    }
})
