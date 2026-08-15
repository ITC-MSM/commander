package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.Overkill
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Overkill — {2}{B} Instant
 * "Target creature gets -0/-9999 until end of turn."
 *
 * The toughness reduction is lethal to any creature; it dies as a state-based action.
 */
class OverkillTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(Overkill)
        return driver
    }

    test("destroys any creature via lethal toughness reduction") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 30), startingLife = 20)

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val target = driver.putCreatureOnBattlefield(opponent, "Force of Nature") // a 5/5

        val overkill = driver.putCardInHand(activePlayer, "Overkill")
        driver.giveMana(activePlayer, Color.BLACK, 1)
        driver.giveColorlessMana(activePlayer, 2)
        driver.castSpell(activePlayer, overkill, targets = listOf(target))
        driver.bothPass()

        driver.findPermanent(opponent, "Force of Nature") shouldBe null
        driver.getGraveyardCardNames(opponent).contains("Force of Nature") shouldBe true
    }
})
