package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.DragoonsWyvern
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Dragoon's Wyvern — {2}{U} 2/1 Creature with flying
 * "When this creature enters, create a 1/1 colorless Hero creature token."
 */
class DragoonsWyvernTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(DragoonsWyvern)
        return driver
    }

    test("creates a 1/1 Hero token when it enters") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 30), startingLife = 20)

        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.getCreatures(player).size shouldBe 0

        val wyvern = driver.putCardInHand(player, "Dragoon's Wyvern")
        driver.giveMana(player, Color.BLUE, 1)
        driver.giveColorlessMana(player, 2)
        driver.castSpell(player, wyvern)
        driver.bothPass() // resolve the creature -> ETB trigger on stack
        driver.bothPass() // resolve the ETB trigger -> token created

        // Wyvern + the Hero token.
        driver.getCreatures(player).size shouldBe 2
    }
})
