package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.CombatResolutionDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.FearOfLostTeeth
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Fear of Lost Teeth (DSK #97) — "When this creature dies, it deals 1 damage to any target and
 * you gain 1 life."
 *
 * Exercises the dies trigger composing `DealDamage(1, any target)` + `GainLife(1)`: when the 1/1
 * Nightmare is dealt lethal combat damage, the controller picks any target for the 1 damage and
 * gains 1 life.
 */
class FearOfLostTeethScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(FearOfLostTeeth))
        return d
    }

    test("dies trigger deals 1 damage to the chosen target and the controller gains 1 life") {
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val nightmare = driver.putCreatureOnBattlefield(p1, "Fear of Lost Teeth")
        driver.removeSummoningSickness(nightmare)
        // A 2/2 blocker that will trade lethal damage with the 1/1 Nightmare.
        val blocker = driver.putCreatureOnBattlefield(p2, "Grizzly Bears")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(p1, listOf(nightmare), p2).error shouldBe null
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(p2, mapOf(blocker to listOf(nightmare))).error shouldBe null
        driver.passPriorityUntil(Step.COMBAT_DAMAGE)
        if (driver.pendingDecision is CombatResolutionDecision) {
            driver.confirmCombatDamage()
        }

        // The Nightmare died → its dies trigger goes on the stack and pauses for a target.
        require(driver.pendingDecision is ChooseTargetsDecision) {
            "Expected a target decision for the dies trigger, got ${driver.pendingDecision}"
        }
        driver.submitTargetSelection(p1, listOf(p2)).error shouldBe null
        driver.bothPass() // resolve the dies trigger

        driver.getLifeTotal(p1) shouldBe 21 // gained 1 life
        driver.getLifeTotal(p2) shouldBe 19 // took 1 damage from the dies trigger
    }
})
