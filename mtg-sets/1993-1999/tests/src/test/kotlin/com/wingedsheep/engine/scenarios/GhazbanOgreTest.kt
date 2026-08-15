package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Ghazbán Ogre's control-changing triggered ability.
 *
 * Ghazbán Ogre: {G} 2/2 Creature — Ogre
 * "At the beginning of your upkeep, if a player has more life than each other player,
 * the player with the most life gains control of this creature."
 */
class GhazbanOgreTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        return driver
    }

    fun advanceToPlayerUpkeep(driver: GameTestDriver, targetPlayer: EntityId) {
        driver.passPriorityUntil(Step.DRAW, maxPasses = 200)
        if (driver.activePlayer == targetPlayer) {
            driver.passPriorityUntil(Step.DRAW, maxPasses = 200)
        }
        driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        driver.currentStep shouldBe Step.UPKEEP
        driver.activePlayer shouldBe targetPlayer
    }

    test("control changes to the player with the most life on upkeep") {
        val driver = createDriver()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val owner = driver.activePlayer!!
        val opponent = driver.getOpponent(owner)

        val ogre = driver.putCreatureOnBattlefield(owner, "Ghazbán Ogre")
        driver.removeSummoningSickness(ogre)

        // Opponent has strictly more life.
        driver.setLifeTotal(owner, 15)
        driver.setLifeTotal(opponent, 20)

        advanceToPlayerUpkeep(driver, owner)
        driver.stackSize shouldBe 1
        driver.bothPass()

        val projected = projector.project(driver.state)
        projected.getController(ogre) shouldBe opponent
    }

    test("no control change when life totals are tied") {
        val driver = createDriver()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val owner = driver.activePlayer!!
        val opponent = driver.getOpponent(owner)

        val ogre = driver.putCreatureOnBattlefield(owner, "Ghazbán Ogre")
        driver.removeSummoningSickness(ogre)

        driver.setLifeTotal(owner, 18)
        driver.setLifeTotal(opponent, 18)

        advanceToPlayerUpkeep(driver, owner)
        driver.bothPass()

        val projected = projector.project(driver.state)
        projected.getController(ogre) shouldBe owner
    }

    test("control moves to the opponent, then returns when the owner regains the lead") {
        val driver = createDriver()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val owner = driver.activePlayer!!
        val opponent = driver.getOpponent(owner)

        val ogre = driver.putCreatureOnBattlefield(owner, "Ghazbán Ogre")
        driver.removeSummoningSickness(ogre)

        // Owner's upkeep: opponent is ahead, so control moves to the opponent.
        driver.setLifeTotal(owner, 15)
        driver.setLifeTotal(opponent, 20)
        advanceToPlayerUpkeep(driver, owner)
        driver.stackSize shouldBe 1
        driver.bothPass()
        projector.project(driver.state).getController(ogre) shouldBe opponent

        // Opponent's upkeep: owner is now ahead, so control returns to the owner.
        // The stale Layer.CONTROL floating effect from the prior change must be replaced,
        // not stacked, so the projected controller flips cleanly back.
        driver.setLifeTotal(owner, 22)
        driver.setLifeTotal(opponent, 18)
        advanceToPlayerUpkeep(driver, opponent)
        driver.stackSize shouldBe 1
        driver.bothPass()
        projector.project(driver.state).getController(ogre) shouldBe owner
    }

    test("controller keeps the ogre when they have the most life") {
        val driver = createDriver()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val owner = driver.activePlayer!!
        val opponent = driver.getOpponent(owner)

        val ogre = driver.putCreatureOnBattlefield(owner, "Ghazbán Ogre")
        driver.removeSummoningSickness(ogre)

        driver.setLifeTotal(owner, 20)
        driver.setLifeTotal(opponent, 12)

        advanceToPlayerUpkeep(driver, owner)
        driver.bothPass()

        val projected = projector.project(driver.state)
        projected.getController(ogre) shouldBe owner
    }
})
