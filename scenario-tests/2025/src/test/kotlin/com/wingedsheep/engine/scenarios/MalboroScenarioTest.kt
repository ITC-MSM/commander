package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Malboro (FIN #106).
 *
 * Malboro {4}{B}{B} Creature — Plant Horror 4/4
 * Bad Breath — When this creature enters, each opponent discards a card, loses 2 life, and
 * exiles the top three cards of their library.
 * Swampcycling {2}
 *
 * Exercises the new [com.wingedsheep.sdk.dsl.LibraryPatterns.exileTop] facade and the
 * per-opponent ForEachPlayer composition (discard + lose life + exile-top all rebind onto
 * the iterated opponent).
 */
class MalboroScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    test("Bad Breath ETB: opponent discards 1, loses 2 life, exiles top 3") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Give the opponent a card in hand to discard (on top of their opening hand).
        driver.putCardInHand(opponent, "Grizzly Bears")
        val handBefore = driver.getHandSize(opponent)
        val lifeBefore = driver.getLifeTotal(opponent)
        val exileBefore = driver.getExile(opponent).size

        val malboro = driver.putCardInHand(active, "Malboro")
        driver.giveMana(active, Color.BLACK, 2)
        driver.giveColorlessMana(active, 4)

        driver.castSpell(active, malboro).isSuccess shouldBe true
        driver.bothPass() // resolve the creature; Bad Breath trigger goes on the stack
        driver.bothPass() // resolve the trigger; it pauses for the opponent's discard choice

        // The opponent must choose a card to discard.
        val decision = driver.pendingDecision
        (decision is SelectCardsDecision) shouldBe true
        val toDiscard = (decision as SelectCardsDecision).options.take(1)
        driver.submitCardSelection(opponent, toDiscard)
        driver.bothPass() // resolve the rest of the trigger

        driver.getHandSize(opponent) shouldBe handBefore - 1
        driver.getLifeTotal(opponent) shouldBe lifeBefore - 2
        driver.getExile(opponent).size shouldBe exileBefore + 3
    }
})
