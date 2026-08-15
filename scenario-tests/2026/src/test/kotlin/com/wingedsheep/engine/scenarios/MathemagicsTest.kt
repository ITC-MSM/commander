package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.Mathemagics
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Mathemagics {X}{X}{U}{U} — Sorcery.
 * "Target player draws 2ˣ cards."
 *
 * Exercises the new `DynamicAmount.Power(2, XValue)`: the number of cards drawn is 2 raised to
 * the chosen X. X=0 draws 1, X=2 draws 4. Also verifies the draw can be aimed at a target player.
 */
class MathemagicsTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(Mathemagics))
        return driver
    }

    test("X=2 draws 2^2 = 4 cards for the targeted player") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingLife = 20)
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val spell = driver.putCardInHand(me, "Mathemagics")
        // {X}{X}{U}{U} with X=2 = {2}{2}{U}{U} -> 6 lands of which 2 must be blue.
        repeat(6) { driver.putLandOnBattlefield(me, "Island") }
        val handBefore = driver.getHandSize(me) - 1 // minus the Mathemagics being cast

        driver.submit(
            CastSpell(
                playerId = me,
                cardId = spell,
                targets = listOf(ChosenTarget.Player(me)),
                xValue = 2,
                paymentStrategy = PaymentStrategy.AutoPay
            )
        )
        driver.bothPass()

        driver.getHandSize(me) shouldBe handBefore + 4
    }

    test("X=0 draws 2^0 = 1 card") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingLife = 20)
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val spell = driver.putCardInHand(me, "Mathemagics")
        repeat(2) { driver.putLandOnBattlefield(me, "Island") }
        val handBefore = driver.getHandSize(me) - 1

        driver.submit(
            CastSpell(
                playerId = me,
                cardId = spell,
                targets = listOf(ChosenTarget.Player(me)),
                xValue = 0,
                paymentStrategy = PaymentStrategy.AutoPay
            )
        )
        driver.bothPass()

        driver.getHandSize(me) shouldBe handBefore + 1
    }
})
