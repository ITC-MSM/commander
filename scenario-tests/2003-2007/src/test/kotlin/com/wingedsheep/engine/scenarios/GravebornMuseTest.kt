package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lgn.cards.GravebornMuse
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.core.Subtype
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Graveborn Muse:
 * {2}{B}{B} Creature — Zombie Spirit 3/3
 * At the beginning of your upkeep, you draw X cards and you lose X life,
 * where X is the number of Zombies you control.
 */
class GravebornMuseTest : FunSpec({

    val TestZombie = CardDefinition.creature(
        name = "Test Zombie",
        manaCost = com.wingedsheep.sdk.core.ManaCost.parse("{1}{B}"),
        subtypes = setOf(Subtype("Zombie")),
        power = 2,
        toughness = 2
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(GravebornMuse, TestZombie))
        return driver
    }

    fun advanceToControllerUpkeep(driver: GameTestDriver, controller: EntityId) {
        driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        if (driver.activePlayer != controller) {
            driver.passPriorityUntil(Step.DRAW, maxPasses = 200)
            driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        }
        driver.currentStep shouldBe Step.UPKEEP
        driver.activePlayer shouldBe controller
    }

    test("draws and loses life equal to number of zombies controlled") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40))

        val controller = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Graveborn Muse is itself a Zombie, plus add another
        driver.putCreatureOnBattlefield(controller, "Graveborn Muse")
        driver.putCreatureOnBattlefield(controller, "Test Zombie")

        val handSizeBefore = driver.getHandSize(controller)
        val lifeBefore = driver.getLifeTotal(controller)

        // Advance to controller's upkeep
        advanceToControllerUpkeep(driver, controller)

        // Trigger should be on the stack (2 zombies = draw 2, lose 2 life)
        driver.stackSize shouldBe 1
        driver.bothPass()

        driver.getHandSize(controller) shouldBe handSizeBefore + 2
        driver.getLifeTotal(controller) shouldBe lifeBefore - 2
    }

    test("with only Graveborn Muse as zombie, draws 1 and loses 1") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40))

        val controller = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Only Graveborn Muse itself (1 Zombie)
        driver.putCreatureOnBattlefield(controller, "Graveborn Muse")

        val handSizeBefore = driver.getHandSize(controller)
        val lifeBefore = driver.getLifeTotal(controller)

        advanceToControllerUpkeep(driver, controller)
        driver.stackSize shouldBe 1
        driver.bothPass()

        driver.getHandSize(controller) shouldBe handSizeBefore + 1
        driver.getLifeTotal(controller) shouldBe lifeBefore - 1
    }

    test("does not trigger during opponent's upkeep") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40))

        val controller = driver.activePlayer!!
        val opponent = driver.getOpponent(controller)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(controller, "Graveborn Muse")

        val lifeBefore = driver.getLifeTotal(controller)

        // Advance to opponent's upkeep
        driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        driver.activePlayer shouldBe opponent

        // Should not trigger
        driver.stackSize shouldBe 0
        driver.getLifeTotal(controller) shouldBe lifeBefore
    }
})
