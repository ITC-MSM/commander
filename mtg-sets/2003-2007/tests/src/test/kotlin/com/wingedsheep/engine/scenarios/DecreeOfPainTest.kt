package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CycleCard
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tests for Decree of Pain:
 * {6}{B}{B} Sorcery
 * Destroy all creatures. They can't be regenerated. Draw a card for each creature destroyed this way.
 * Cycling {3}{B}{B}
 * When you cycle Decree of Pain, all creatures get -2/-2 until end of turn.
 */
class DecreeOfPainTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    test("destroys all creatures and draws cards for each") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 40),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Put creatures on both sides
        driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")
        driver.putCreatureOnBattlefield(activePlayer, "Glory Seeker")
        driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        val handSizeBefore = driver.getHandSize(activePlayer)

        // Cast Decree of Pain
        val decree = driver.putCardInHand(activePlayer, "Decree of Pain")
        driver.giveMana(activePlayer, Color.BLACK, 2)
        driver.giveColorlessMana(activePlayer, 6)
        driver.castSpell(activePlayer, decree)
        driver.bothPass()

        // All 3 creatures should be destroyed
        driver.findPermanent(activePlayer, "Grizzly Bears") shouldBe null
        driver.findPermanent(activePlayer, "Glory Seeker") shouldBe null
        driver.findPermanent(opponent, "Grizzly Bears") shouldBe null

        // Should have drawn 3 cards (one for each creature destroyed)
        driver.getHandSize(activePlayer) shouldBe handSizeBefore + 3
    }

    test("draws zero cards when no creatures on battlefield") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 40),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val handSizeBefore = driver.getHandSize(activePlayer)

        val decree = driver.putCardInHand(activePlayer, "Decree of Pain")
        driver.giveMana(activePlayer, Color.BLACK, 2)
        driver.giveColorlessMana(activePlayer, 6)
        driver.castSpell(activePlayer, decree)
        driver.bothPass()

        // No cards drawn
        driver.getHandSize(activePlayer) shouldBe handSizeBefore
    }

    test("cycling trigger gives all creatures -2/-2") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 40),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Put a 2/2 creature — should die from -2/-2
        driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        // Put a larger creature that survives -2/-2
        driver.putCreatureOnBattlefield(activePlayer, "Elvish Aberration")

        // Cycle Decree of Pain
        val decree = driver.putCardInHand(activePlayer, "Decree of Pain")
        driver.giveMana(activePlayer, Color.BLACK, 2)
        driver.giveColorlessMana(activePlayer, 3)

        driver.submit(CycleCard(playerId = activePlayer, cardId = decree))

        // Cycling trigger goes on stack — resolve it
        driver.bothPass()

        // Grizzly Bears (2/2) should die from -2/-2
        driver.findPermanent(opponent, "Grizzly Bears") shouldBe null

        // Elvish Aberration (4/5 after -2/-2 = 2/3) should survive
        driver.findPermanent(activePlayer, "Elvish Aberration") shouldNotBe null
    }
})
