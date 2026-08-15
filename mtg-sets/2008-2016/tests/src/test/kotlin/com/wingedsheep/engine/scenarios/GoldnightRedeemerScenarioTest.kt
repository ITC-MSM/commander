package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.avr.cards.GoldnightRedeemer
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class GoldnightRedeemerScenarioTest : FunSpec({
    test("ETB gains 2 life per other creature you control") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(GoldnightRedeemer)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Two other creatures already on the battlefield
        driver.putCreatureOnBattlefield(you, "Grizzly Bears")
        driver.putCreatureOnBattlefield(you, "Grizzly Bears")

        val card = driver.putCardInHand(you, "Goldnight Redeemer")
        driver.giveMana(you, Color.WHITE, 2)
        driver.giveColorlessMana(you, 4)
        driver.castSpell(you, card)
        driver.bothPass() // resolve creature
        driver.bothPass() // resolve ETB gain life

        // 2 other creatures * 2 life = +4 (self excluded)
        driver.getLifeTotal(you) shouldBe 24
        driver.findPermanent(you, "Goldnight Redeemer") shouldNotBe null
    }
})
