package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lgn.cards.CallerOfTheClaw
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Caller of the Claw:
 * {2}{G} Creature — Elf 2/2
 * Flash
 * When Caller of the Claw enters the battlefield, create a 2/2 green Bear creature
 * token for each nontoken creature put into your graveyard from the battlefield this turn.
 */
class CallerOfTheClawTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(CallerOfTheClaw))
        return driver
    }

    test("creates bear tokens equal to nontoken creatures that died this turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 20, "Mountain" to 20))

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Put creatures on battlefield, then kill them
        val bears1 = driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")
        val bears2 = driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")

        // Kill both with Lightning Bolts
        val bolt1 = driver.putCardInHand(activePlayer, "Lightning Bolt")
        val bolt2 = driver.putCardInHand(activePlayer, "Lightning Bolt")
        driver.giveMana(activePlayer, Color.RED, 2)

        driver.castSpell(activePlayer, bolt1, listOf(bears1))
        driver.bothPass()
        driver.castSpell(activePlayer, bolt2, listOf(bears2))
        driver.bothPass()

        // Both creatures should be dead
        val creaturesBeforeCaller = driver.getCreatures(activePlayer).size

        // Now cast Caller of the Claw (has flash)
        val caller = driver.putCardInHand(activePlayer, "Caller of the Claw")
        driver.giveMana(activePlayer, Color.GREEN, 3)
        driver.castSpell(activePlayer, caller)
        driver.bothPass() // resolve Caller

        // ETB trigger should be on the stack
        driver.stackSize shouldBe 1
        driver.bothPass() // resolve trigger

        // Should have created 2 Bear tokens (one for each dead nontoken creature)
        // Caller itself (1) + 2 bear tokens = 3 creatures
        val creatures = driver.getCreatures(activePlayer)
        creatures.size shouldBe 3 // Caller + 2 bears
    }

    test("creates zero tokens when no creatures died this turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Cast Caller of the Claw with no creatures having died
        val caller = driver.putCardInHand(activePlayer, "Caller of the Claw")
        driver.giveMana(activePlayer, Color.GREEN, 3)
        driver.castSpell(activePlayer, caller)
        driver.bothPass() // resolve Caller

        // ETB trigger resolves
        driver.stackSize shouldBe 1
        driver.bothPass()

        // Only Caller itself, no bear tokens
        driver.getCreatures(activePlayer).size shouldBe 1
    }
})
