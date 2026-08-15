package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.mana.CostCalculator
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.OrysaTideChoreographer
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Orysa, Tide Choreographer {4}{U}: "This spell costs {3} less to cast if creatures you control
 * have total toughness 10 or greater." Modelled as a self [ModifySpellCost] gated on an
 * [CostGating.OnlyIf] condition summing projected creature toughness.
 */
class OrysaTideChoreographerScenarioTest : FunSpec({

    fun registry(): CardRegistry {
        val r = CardRegistry()
        r.register(TestCards.all)
        r.register(OrysaTideChoreographer)
        return r
    }

    test("costs {3} less when creatures you control have total toughness >= 10") {
        val reg = registry()
        val calculator = CostCalculator(reg)
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(OrysaTideChoreographer))
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Two Force of Nature (toughness 5 each) → total toughness 10.
        driver.putCreatureOnBattlefield(me, "Force of Nature")
        driver.putCreatureOnBattlefield(me, "Force of Nature")

        // {4}{U} → {1}{U}: generic 4 - 3 = 1.
        val orysa = reg.requireCard("Orysa, Tide Choreographer")
        val cost = calculator.calculateEffectiveCost(driver.state, orysa, me)
        cost.genericAmount shouldBe 1
    }

    test("not reduced when total toughness is below 10") {
        val reg = registry()
        val calculator = CostCalculator(reg)
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(OrysaTideChoreographer))
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // One Force of Nature (toughness 5) → total toughness 5, below the threshold.
        driver.putCreatureOnBattlefield(me, "Force of Nature")

        val orysa = reg.requireCard("Orysa, Tide Choreographer")
        val cost = calculator.calculateEffectiveCost(driver.state, orysa, me)
        cost.genericAmount shouldBe 4
    }
})
