package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Cautious Survivor (DSK #172) — {3}{G} 4/4 Creature — Elf Survivor.
 *
 * "Survival — At the beginning of your second main phase, if this creature is tapped, you gain
 *  2 life."
 *
 * The Survival ability word is purely flavor — it adds no keyword. The card is an intervening-if
 * triggered ability on the second (postcombat) main phase, gated on this creature being tapped.
 */
class CautiousSurvivorScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("gains 2 life at second main phase while tapped") {
        val driver = newDriver()
        val player = driver.player1
        val lifeBefore = driver.getLifeTotal(player)

        val survivor = driver.putCreatureOnBattlefield(player, "Cautious Survivor")
        driver.tapPermanent(survivor)

        // Advance to the controller's postcombat (second) main phase. The Survival trigger fires
        // at the beginning of the step and goes on the stack; pass priority so it resolves.
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)
        driver.bothPass()

        driver.getLifeTotal(player) shouldBe lifeBefore + 2
    }

    test("does not gain life if the creature is untapped at second main phase") {
        val driver = newDriver()
        val player = driver.player1
        val lifeBefore = driver.getLifeTotal(player)

        // Untapped Cautious Survivor — the intervening-if (this creature is tapped) is false.
        driver.putCreatureOnBattlefield(player, "Cautious Survivor")

        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        driver.getLifeTotal(player) shouldBe lifeBefore
    }
})
