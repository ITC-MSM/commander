package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Arbiter of Knollridge (LRW #2) — {6}{W} 5/5 Giant Wizard with vigilance.
 *
 * "When this creature enters, each player's life total becomes the highest life total among
 *  all players."
 *
 * The interesting claim is that the amount is a per-player *maximum*, not a sum, and that it
 * lands on every seat rather than just the controller — so the test runs it from behind on life
 * (the controller gains) and from ahead (the opponent gains, the controller is untouched).
 */
class ArbiterOfKnollridgeScenarioTest : FunSpec({

    test("the controller behind on life is raised to the opponent's total") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.setLifeTotal(player, 4)
        driver.setLifeTotal(opponent, 17)

        val arbiter = driver.putCardInHand(player, "Arbiter of Knollridge")
        driver.giveMana(player, Color.WHITE, 7)
        driver.castSpell(player, arbiter)
        driver.bothPass() // resolve the spell → it enters → the trigger goes on the stack
        driver.bothPass() // resolve the trigger

        driver.getLifeTotal(player) shouldBe 17
        driver.getLifeTotal(opponent) shouldBe 17
    }

    test("the opponent behind on life is raised; a sum would overshoot both") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.setLifeTotal(player, 23)
        driver.setLifeTotal(opponent, 6)

        val arbiter = driver.putCardInHand(player, "Arbiter of Knollridge")
        driver.giveMana(player, Color.WHITE, 7)
        driver.castSpell(player, arbiter)
        driver.bothPass()
        driver.bothPass()

        // 23 is the maximum; the sum of the two totals (29) is what a summed reading would give.
        driver.getLifeTotal(player) shouldBe 23
        driver.getLifeTotal(opponent) shouldBe 23
    }
})
