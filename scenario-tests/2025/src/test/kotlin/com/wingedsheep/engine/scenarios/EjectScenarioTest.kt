package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.Eject
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tests for Eject.
 *
 * Eject: {3}{U}
 * Instant
 * This spell can't be countered.
 * Return target nonland permanent to its owner's hand.
 * Draw a card.
 */
class EjectScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(Eject))
        return driver
    }

    test("Eject bounces target nonland permanent to owner's hand and draws a card") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Island" to 20, "Forest" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bears = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        driver.findPermanent(opponent, "Grizzly Bears") shouldNotBe null

        val handBefore = driver.getHandSize(activePlayer)

        val eject = driver.putCardInHand(activePlayer, "Eject")
        driver.giveMana(activePlayer, Color.BLUE, 1)
        driver.giveColorlessMana(activePlayer, 3)

        val castResult = driver.castSpell(activePlayer, eject, listOf(bears))
        castResult.isSuccess shouldBe true

        driver.bothPass()

        // Creature returned to its owner's hand
        driver.findPermanent(opponent, "Grizzly Bears") shouldBe null
        driver.findCardInHand(opponent, "Grizzly Bears") shouldNotBe null

        // Caster drew a card (Eject itself left hand, so net hand size = handBefore + 1 drawn)
        driver.getHandSize(activePlayer) shouldBe handBefore + 1
    }

    test("Eject cannot target a land") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Island" to 20, "Forest" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val land = driver.putLandOnBattlefield(activePlayer, "Forest")
        val eject = driver.putCardInHand(activePlayer, "Eject")
        driver.giveMana(activePlayer, Color.BLUE, 1)
        driver.giveColorlessMana(activePlayer, 3)

        val castResult = driver.castSpell(activePlayer, eject, listOf(land))
        castResult.isSuccess shouldBe false
    }
})
