package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sth.cards.BottomlessPit
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Bottomless Pit (STH) — {1}{B}{B} Enchantment.
 *
 * "At the beginning of each player's upkeep, that player discards a card at random."
 *
 * Pins the each-player upkeep scope (`Triggers.EachUpkeep`) drafted by mtgish-tooling: the trigger
 * fires on EVERY player's upkeep — its controller's AND its opponent's — and the discarding player
 * is the player whose upkeep it is. The discard is at random, so no decision is required.
 */
class BottomlessPitScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(BottomlessPit))
        return driver
    }

    // Advance to the next upkeep belonging to [player] (skips one turn if we're on the other player's).
    fun advanceToUpkeepOf(driver: GameTestDriver, player: EntityId) {
        driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        if (driver.activePlayer != player) {
            driver.passPriorityUntil(Step.DRAW, maxPasses = 200)
            driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        }
        driver.currentStep shouldBe Step.UPKEEP
        driver.activePlayer shouldBe player
    }

    test("its controller discards a card at random on their own upkeep") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40))

        val controller = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putPermanentOnBattlefield(controller, "Bottomless Pit")

        // Advance to the controller's NEXT upkeep; the trigger is on the stack.
        advanceToUpkeepOf(driver, controller)
        driver.stackSize shouldBe 1

        val handBefore = driver.getHandSize(controller)
        val graveBefore = driver.getGraveyard(controller).size
        driver.bothPass() // random discard — resolves with no decision

        driver.getHandSize(controller) shouldBe handBefore - 1
        driver.getGraveyard(controller).size shouldBe graveBefore + 1
    }

    test("each opponent also discards at random on their upkeep") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40))

        val controller = driver.activePlayer!!
        val opponent = driver.getOpponent(controller)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putPermanentOnBattlefield(controller, "Bottomless Pit")

        // The opponent's upkeep (turn 2) comes before the controller's next one.
        advanceToUpkeepOf(driver, opponent)
        driver.stackSize shouldBe 1

        val handBefore = driver.getHandSize(opponent)
        val graveBefore = driver.getGraveyard(opponent).size
        driver.bothPass()

        driver.getHandSize(opponent) shouldBe handBefore - 1
        driver.getGraveyard(opponent).size shouldBe graveBefore + 1
    }
})
