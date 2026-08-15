package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.avr.cards.GryffVanguard
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class GryffVanguardScenarioTest : FunSpec({
    test("ETB draws a card") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(GryffVanguard)
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val beforeHand = driver.getHandSize(you)
        val card = driver.putCardInHand(you, "Gryff Vanguard")
        driver.giveMana(you, Color.BLUE, 1)
        driver.giveColorlessMana(you, 4)
        driver.castSpell(you, card)
        driver.bothPass() // resolve creature
        driver.bothPass() // resolve ETB draw

        // Hand: put card (+1) then cast (-1) then draw (+1) => +1 vs beforeHand
        driver.getHandSize(you) shouldBe beforeHand + 1
        driver.findPermanent(you, "Gryff Vanguard") shouldNotBe null
    }
})
