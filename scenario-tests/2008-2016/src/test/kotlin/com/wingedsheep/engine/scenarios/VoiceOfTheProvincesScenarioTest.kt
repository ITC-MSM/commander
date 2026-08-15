package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.avr.cards.VoiceOfTheProvinces
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class VoiceOfTheProvincesScenarioTest : FunSpec({
    test("ETB creates a 1/1 white Human token") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(VoiceOfTheProvinces)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val card = driver.putCardInHand(you, "Voice of the Provinces")
        driver.giveMana(you, Color.WHITE, 2)
        driver.giveColorlessMana(you, 4)
        driver.castSpell(you, card)
        driver.bothPass() // resolve angel
        driver.bothPass() // resolve ETB token

        driver.findPermanent(you, "Voice of the Provinces") shouldNotBe null
        driver.getCreatures(you).size shouldBe 2
    }
})
