package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario test for End of the Hunt (SOS #81) — {1}{B} Sorcery.
 *
 * "Target opponent exiles a creature or planeswalker they control with the greatest mana value
 *  among creatures and planeswalkers they control."
 *
 * Composed from atomic pipeline primitives (Gather → FilterCollection(GreatestManaValue) →
 * SelectFromCollection(by target player) → MoveCollection(exile)).
 */
class EndOfTheHuntScenarioTest : FunSpec({

    fun GameTestDriver.resolveStack() {
        var safety = 0
        while (stackSize > 0 && !isPaused && safety < 20) {
            bothPass(); safety++
        }
    }

    test("the greatest-mana-value creature is auto-exiled when it is the unique highest") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Opponent controls a MV 2 creature and a MV 4 creature — only the MV 4 one is exiled.
        driver.putCreatureOnBattlefield(opponent, "Grizzly Bears") // MV 2
        driver.putCreatureOnBattlefield(opponent, "Hill Giant")    // MV 4 — highest

        repeat(2) { driver.putLandOnBattlefield(player, "Swamp") }

        val hunt = driver.putCardInHand(player, "End of the Hunt")
        driver.submitSuccess(
            CastSpell(
                playerId = player,
                cardId = hunt,
                paymentStrategy = PaymentStrategy.AutoPay,
                targets = listOf(entityIdToChosenTarget(driver.state, opponent))
            )
        )
        driver.resolveStack()

        // Hill Giant (greatest MV) exiled; Grizzly Bears (lower MV) untouched.
        (driver.findPermanent(opponent, "Hill Giant") == null) shouldBe true
        (driver.findPermanent(opponent, "Grizzly Bears") != null) shouldBe true
        driver.getExileCardNames(opponent).contains("Hill Giant") shouldBe true
    }

    test("does nothing when the opponent controls no creatures or planeswalkers") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        repeat(2) { driver.putLandOnBattlefield(player, "Swamp") }

        val hunt = driver.putCardInHand(player, "End of the Hunt")
        driver.submitSuccess(
            CastSpell(
                playerId = player,
                cardId = hunt,
                paymentStrategy = PaymentStrategy.AutoPay,
                targets = listOf(entityIdToChosenTarget(driver.state, opponent))
            )
        )
        driver.resolveStack()

        // Nothing happened — opponent's life and board are unchanged.
        driver.getLifeTotal(opponent) shouldBe 20
        driver.getExileCardNames(opponent).isEmpty() shouldBe true
    }
})
