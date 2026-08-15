package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.DwarvenCastleGuard
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Dwarven Castle Guard — {1}{W} 2/1 Creature
 * "When this creature dies, create a 1/1 colorless Hero creature token."
 */
class DwarvenCastleGuardTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(DwarvenCastleGuard)
        return driver
    }

    test("creates a 1/1 Hero token when it dies") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 30), startingLife = 20)

        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val guard = driver.putCreatureOnBattlefield(player, "Dwarven Castle Guard")
        driver.getCreatures(player).size shouldBe 1

        // Burn the 2/1 guard to death.
        val bolt = driver.putCardInHand(player, "Lightning Bolt")
        driver.giveMana(player, Color.RED, 1)
        driver.castSpell(player, bolt, targets = listOf(guard))
        driver.bothPass() // resolve the bolt -> guard dies -> dies trigger on stack
        driver.bothPass() // resolve the dies trigger -> Hero token created

        driver.findPermanent(player, "Dwarven Castle Guard") shouldBe null
        driver.getGraveyardCardNames(player).contains("Dwarven Castle Guard") shouldBe true
        // The Hero token replaced the guard.
        driver.getCreatures(player).size shouldBe 1
    }
})
