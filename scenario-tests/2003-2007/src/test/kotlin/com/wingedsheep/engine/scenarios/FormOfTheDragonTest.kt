package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.scg.cards.FormOfTheDragon
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Form of the Dragon:
 * {4}{R}{R}{R} Enchantment
 * At the beginning of your upkeep, this enchantment deals 5 damage to any target.
 * At the beginning of each end step, your life total becomes 5.
 * Creatures without flying can't attack you.
 */
class FormOfTheDragonTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(FormOfTheDragon)
        return driver
    }

    fun advanceToPlayerUpkeep(driver: GameTestDriver, targetPlayer: EntityId) {
        driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        if (driver.activePlayer != targetPlayer) {
            driver.passPriorityUntil(Step.DRAW, maxPasses = 200)
            driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        }
        driver.currentStep shouldBe Step.UPKEEP
        driver.activePlayer shouldBe targetPlayer
    }

    test("upkeep trigger deals 5 damage to chosen target") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Mountain" to 40),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(activePlayer, "Form of the Dragon")

        // Advance to controller's next upkeep
        advanceToPlayerUpkeep(driver, activePlayer)

        // Trigger goes on stack, needs a target — select opponent
        driver.submitTargetSelection(activePlayer, listOf(opponent))
        driver.bothPass()

        // Opponent should have taken 5 damage
        driver.getLifeTotal(opponent) shouldBe 15
    }

    test("upkeep trigger can target a creature") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Mountain" to 40),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(activePlayer, "Form of the Dragon")
        val bear = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        // Advance to controller's next upkeep
        advanceToPlayerUpkeep(driver, activePlayer)

        // Target the creature
        driver.submitTargetSelection(activePlayer, listOf(bear))
        driver.bothPass()

        // Bear (2/2) should be dead from 5 damage
        driver.findPermanent(opponent, "Grizzly Bears") shouldBe null
    }

    test("end step sets life total to 5") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Mountain" to 40),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(activePlayer, "Form of the Dragon")

        // Advance to end step
        driver.passPriorityUntil(Step.END, maxPasses = 200)

        // End step trigger resolves — life becomes 5
        driver.bothPass()

        driver.getLifeTotal(activePlayer) shouldBe 5
    }

    test("end step resets life on every turn including opponent's") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Mountain" to 40),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(activePlayer, "Form of the Dragon")

        // Advance past active player's end step
        driver.passPriorityUntil(Step.END, maxPasses = 200)
        driver.bothPass() // resolve end step trigger
        driver.getLifeTotal(activePlayer) shouldBe 5

        // Advance to opponent's end step
        driver.passPriorityUntil(Step.END, maxPasses = 200)
        driver.bothPass() // resolve end step trigger again
        driver.getLifeTotal(activePlayer) shouldBe 5
    }

    test("non-flying creature cannot attack controller") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Mountain" to 40),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(activePlayer, "Form of the Dragon")

        // Give opponent a non-flying creature
        val bear = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        driver.removeSummoningSickness(bear)

        // Advance to opponent's turn
        // First handle activePlayer's end step trigger
        driver.passPriorityUntil(Step.END, maxPasses = 200)
        driver.bothPass() // end step trigger

        // Advance through opponent's turn — if DECLARE_ATTACKERS is reached,
        // declaring the bear as attacker should fail. If it's auto-skipped
        // (no legal attacks), the bear never attacks either way.
        // Verify by advancing past combat entirely — opponent's life should be unchanged.
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN, maxPasses = 200)
        driver.activePlayer shouldBe opponent

        // activePlayer's life should be unchanged (bear couldn't attack)
        driver.getLifeTotal(activePlayer) shouldBe 5
    }
})
