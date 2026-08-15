package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mom.cards.CorruptedConviction
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Corrupted Conviction: {B} Instant
 * As an additional cost to cast this spell, sacrifice a creature.
 * Draw two cards.
 */
class CorruptedConvictionTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(CorruptedConviction))
        return driver
    }

    test("sacrifices a creature as an additional cost and draws two cards") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        val fodder = driver.putCreatureOnBattlefield(me, "Centaur Courser")
        val spell = driver.putCardInHand(me, "Corrupted Conviction")
        driver.giveMana(me, Color.BLACK, 1)

        val handBefore = driver.getHandSize(me)

        val result = driver.submit(
            CastSpell(
                playerId = me,
                cardId = spell,
                additionalCostPayment = AdditionalCostPayment(sacrificedPermanents = listOf(fodder)),
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        result.isSuccess shouldBe true
        driver.bothPass()

        // The sacrificed creature went to the graveyard.
        driver.findPermanent(me, "Centaur Courser") shouldBe null
        driver.assertInGraveyard(me, "Centaur Courser")

        // Net hand change: -1 (the spell cast) + 2 (drawn) = +1.
        driver.getHandSize(me) shouldBe handBefore + 1
    }
})
