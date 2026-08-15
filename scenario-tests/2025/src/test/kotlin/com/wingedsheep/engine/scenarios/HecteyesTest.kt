package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.Hecteyes
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Hecteyes — {1}{B} 1/1 Creature
 * "When this creature enters, each opponent discards a card."
 */
class HecteyesTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(Hecteyes)
        return driver
    }

    test("each opponent discards a card on enter") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 30), startingLife = 20)

        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val oppHandBefore = driver.getHandSize(opponent)

        val hecteyes = driver.putCardInHand(player, "Hecteyes")
        driver.giveMana(player, Color.BLACK, 1)
        driver.giveColorlessMana(player, 1)
        driver.castSpell(player, hecteyes)
        driver.bothPass() // resolve the creature -> ETB trigger on stack
        driver.bothPass() // resolve the ETB trigger -> opponent must discard

        // Opponent chooses which card to discard.
        val decision = driver.pendingDecision
        if (decision is SelectCardsDecision) {
            driver.submitCardSelection(opponent, listOf(driver.getHand(opponent).first()))
        }

        driver.getHandSize(opponent) shouldBe (oppHandBefore - 1)
    }
})
