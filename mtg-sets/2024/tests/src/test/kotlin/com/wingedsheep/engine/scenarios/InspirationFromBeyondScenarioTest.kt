package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Inspiration from Beyond (FDN #43) — {2}{U} Sorcery, Flashback {5}{U}{U}.
 *
 *   "Mill three cards, then return an instant or sorcery card from your graveyard to your hand."
 *
 * Verifies the mill-then-recur pipeline: three cards are milled, then the player chooses an
 * instant or sorcery card from the graveyard (including one just milled) to return to hand.
 */
class InspirationFromBeyondScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("mill three, then return a milled instant to hand") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        // Top three (any order): two instants + a land. All three are milled; the two
        // instants are the eligible choices to return.
        driver.putCardOnTopOfLibrary(me, "Forest")
        val bolt1 = driver.putCardOnTopOfLibrary(me, "Lightning Bolt")
        val bolt2 = driver.putCardOnTopOfLibrary(me, "Lightning Bolt")

        val spell = driver.putCardInHand(me, "Inspiration from Beyond")
        driver.giveMana(me, Color.BLUE, 3) // {2}{U}

        driver.submit(
            CastSpell(playerId = me, cardId = spell, paymentStrategy = PaymentStrategy.AutoPay)
        ).isSuccess shouldBe true
        driver.bothPass() // resolve -> mill three -> pause for the instant/sorcery choice

        driver.isPaused shouldBe true
        val select = driver.pendingDecision
        select.shouldBeInstanceOf<SelectCardsDecision>()
        // Both milled instants are eligible; the milled land is not.
        select.options.size shouldBe 2

        driver.submitDecision(
            me,
            CardsSelectedResponse(decisionId = select.id, selectedCards = listOf(bolt1))
        )
        driver.isPaused shouldBe false

        // Chosen instant returned to hand; the other instant stays in the graveyard.
        driver.getHand(me).contains(bolt1) shouldBe true
        driver.getGraveyard(me).contains(bolt1) shouldBe false
        driver.getGraveyard(me).contains(bolt2) shouldBe true
    }
})
