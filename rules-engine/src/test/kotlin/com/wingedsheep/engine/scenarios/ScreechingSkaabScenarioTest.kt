package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dka.cards.ScreechingSkaab
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class ScreechingSkaabScenarioTest : FunSpec({
    test("ETB mills two cards") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(ScreechingSkaab)
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val beforeGy = driver.getGraveyardCardNames(you).size
        val card = driver.putCardInHand(you, "Screeching Skaab")
        driver.giveMana(you, Color.BLUE, 1)
        driver.giveColorlessMana(you, 1)
        driver.castSpell(you, card)
        driver.bothPass() // resolve creature
        driver.bothPass() // resolve ETB mill

        driver.getGraveyardCardNames(you).size shouldBe beforeGy + 2
        driver.findPermanent(you, "Screeching Skaab") shouldNotBe null
    }
})
