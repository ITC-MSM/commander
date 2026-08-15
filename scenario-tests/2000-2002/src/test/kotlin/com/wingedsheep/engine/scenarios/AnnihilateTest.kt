package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.inv.cards.Annihilate
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Annihilate.
 *
 * Annihilate: {3}{B}{B}
 * Instant
 * Destroy target nonblack creature. It can't be regenerated.
 * Draw a card.
 */
class AnnihilateTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(Annihilate))
        return driver
    }

    test("destroys a nonblack creature and draws a card") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 30, "Forest" to 10), startingLife = 20)

        val player1 = driver.activePlayer!!
        val player2 = driver.getOpponent(player1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bears = driver.putCreatureOnBattlefield(player2, "Grizzly Bears")
        val annihilate = driver.putCardInHand(player1, "Annihilate")
        driver.giveMana(player1, Color.BLACK, 5)
        val handBefore = driver.getHandSize(player1)

        val result = driver.castSpellWithTargets(player1, annihilate, listOf(ChosenTarget.Permanent(bears)))
        result.isSuccess shouldBe true
        driver.bothPass()

        driver.findPermanent(player2, "Grizzly Bears") shouldBe null
        // -1 for casting Annihilate, +1 for the draw
        driver.getHandSize(player1) shouldBe handBefore
    }

    test("cannot target a black creature") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 30, "Forest" to 10), startingLife = 20)

        val player1 = driver.activePlayer!!
        val player2 = driver.getOpponent(player1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val blackCreature = driver.putCreatureOnBattlefield(player2, "Black Creature")
        val annihilate = driver.putCardInHand(player1, "Annihilate")
        driver.giveMana(player1, Color.BLACK, 5)

        val result = driver.castSpellWithTargets(player1, annihilate, listOf(ChosenTarget.Permanent(blackCreature)))
        result.isSuccess shouldBe false
        driver.findPermanent(player2, "Black Creature") shouldBe blackCreature
    }
})
