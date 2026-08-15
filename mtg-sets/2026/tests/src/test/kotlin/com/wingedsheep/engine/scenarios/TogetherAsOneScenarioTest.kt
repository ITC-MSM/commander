package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.TogetherAsOne
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Together as One ({6} sorcery):
 *   "Converge — Target player draws X cards, Together as One deals X damage to any target, and you
 *    gain X life, where X is the number of colors of mana spent to cast this spell."
 *
 * X = `DynamicAmount.DistinctColorsManaSpent`, resolved on the stack. Pins all three effects scaling
 * together, and the X = 0 (all-colourless {6}) degenerate case where the spell still resolves but
 * does nothing.
 */
class TogetherAsOneScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCards(listOf(TogetherAsOne))
        return driver
    }

    fun startTurn(driver: GameTestDriver): EntityId {
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver.activePlayer!!
    }

    test("3 colors spent: target player draws 3, opponent takes 3 damage, you gain 3 life") {
        val driver = createDriver()
        val p = startTurn(driver)
        val opp = driver.getOpponent(p)

        val spell = driver.putCardInHand(p, "Together as One")
        // {6} paid with three distinct colors → X = 3.
        driver.giveMana(p, Color.WHITE, 2)
        driver.giveMana(p, Color.BLUE, 2)
        driver.giveMana(p, Color.RED, 2)

        val handBefore = driver.getHandSize(p)
        val lifeBefore = driver.getLifeTotal(p)
        val oppLifeBefore = driver.getLifeTotal(opp)

        // Target player = the caster (draws X), any target = the opponent (takes X damage).
        driver.castSpell(p, spell, targets = listOf(p, opp)).isSuccess shouldBe true
        driver.bothPass()

        // -1 (cast the sorcery) + 3 (drew X) = +2 net hand.
        driver.getHandSize(p) shouldBe (handBefore - 1 + 3)
        driver.getLifeTotal(p) shouldBe (lifeBefore + 3)
        driver.getLifeTotal(opp) shouldBe (oppLifeBefore - 3)
    }

    test("all colorless: X = 0, no draw, no damage, no life gain") {
        val driver = createDriver()
        val p = startTurn(driver)
        val opp = driver.getOpponent(p)

        val spell = driver.putCardInHand(p, "Together as One")
        driver.giveColorlessMana(p, 6) // 0 colors → X = 0

        val handBefore = driver.getHandSize(p)
        val lifeBefore = driver.getLifeTotal(p)
        val oppLifeBefore = driver.getLifeTotal(opp)

        driver.castSpell(p, spell, targets = listOf(p, opp)).isSuccess shouldBe true
        driver.bothPass()

        driver.getHandSize(p) shouldBe (handBefore - 1) // only the cast sorcery left hand
        driver.getLifeTotal(p) shouldBe lifeBefore
        driver.getLifeTotal(opp) shouldBe oppLifeBefore
    }
})
