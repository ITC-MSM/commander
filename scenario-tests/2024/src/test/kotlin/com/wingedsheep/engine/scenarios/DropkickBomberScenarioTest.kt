package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fdn.cards.DropkickBomber
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Dropkick Bomber (FDN) — {2}{R} Creature — Goblin Warrior 2/3.
 *
 * "Other Goblins you control get +1/+1." The activated ability (grant flying + a temporary
 * combat-damage-sacrifice trigger) reuses proven grant primitives; this test pins the anthem's
 * projection: other Goblins are buffed, Dropkick Bomber itself is not (excludeSelf).
 */
class DropkickBomberScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + DropkickBomber)
        return driver
    }

    test("Dropkick Bomber buffs other Goblins you control but not itself") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bomber = driver.putCreatureOnBattlefield(player, "Dropkick Bomber")
        val goblin = driver.putCreatureOnBattlefield(player, "Goblin Guide") // base 2/1 Goblin

        val projected = projector.project(driver.state)
        // Other Goblin gets +1/+1: 2/1 -> 3/2.
        projected.getPower(goblin) shouldBe 3
        projected.getToughness(goblin) shouldBe 2
        // Dropkick Bomber does not buff itself (excludeSelf).
        projected.getPower(bomber) shouldBe 2
        projected.getToughness(bomber) shouldBe 3
    }

    test("the anthem does not buff a Goblin an opponent controls") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(player, "Dropkick Bomber")
        val enemyGoblin = driver.putCreatureOnBattlefield(opponent, "Goblin Guide")

        val projected = projector.project(driver.state)
        // "Goblins you control" — the opponent's Goblin is unaffected.
        projected.getPower(enemyGoblin) shouldBe 2
        projected.getToughness(enemyGoblin) shouldBe 1
    }
})
