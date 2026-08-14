package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.avr.cards.GoldnightRedeemer
import com.wingedsheep.mtg.sets.definitions.avr.cards.SeraphSanctuary
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class SeraphSanctuaryScenarioTest : FunSpec({
    test("ETB gains 1 life; Angel you control entering gains 1 more") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(SeraphSanctuary)
        driver.registerCard(GoldnightRedeemer)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val land = driver.putCardInHand(you, "Seraph Sanctuary")
        driver.playLand(you, land)
        driver.bothPass() // resolve land ETB

        driver.getLifeTotal(you) shouldBe 21
        driver.findPermanent(you, "Seraph Sanctuary") shouldNotBe null

        // Angel ETB: Sanctuary trigger +1. Redeemer ETB with no other creatures: +0.
        val angel = driver.putCardInHand(you, "Goldnight Redeemer")
        driver.giveMana(you, Color.WHITE, 2)
        driver.giveColorlessMana(you, 4)
        driver.castSpell(you, angel)
        driver.bothPass() // resolve angel (Sanctuary + Redeemer triggers stack)
        driver.bothPass() // resolve one ETB trigger
        driver.bothPass() // resolve the other ETB trigger

        driver.getLifeTotal(you) shouldBe 22
    }
})
