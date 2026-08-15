package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SaddleMount
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Drover Grizzly (OTJ).
 *
 * Oracle: "Whenever this creature attacks while saddled, creatures you control gain trample
 * until end of turn. Saddle 1"
 *
 * The "while saddled" gate is an intervening-if (CR 603.4) on the attack trigger, so the
 * trample grant only happens if the Grizzly was saddled before it attacked.
 */
class DroverGrizzlyScenarioTest : FunSpec({

    val projector = StateProjector()

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("attacking while saddled grants trample to creatures you control") {
        val driver = newDriver()
        val grizzly = driver.putCreatureOnBattlefield(driver.player1, "Drover Grizzly")
        val saddler = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears") // power 2 >= 1
        driver.removeSummoningSickness(grizzly)

        driver.submitSuccess(SaddleMount(driver.player1, grizzly, listOf(saddler)))
        driver.bothPass()

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(driver.player1, listOf(grizzly), driver.player2)
        driver.bothPass() // resolve the attack-while-saddled trigger

        projector.hasProjectedKeyword(driver.state, grizzly, Keyword.TRAMPLE) shouldBe true
        projector.hasProjectedKeyword(driver.state, saddler, Keyword.TRAMPLE) shouldBe true
    }

    test("attacking while NOT saddled does not grant trample") {
        val driver = newDriver()
        val grizzly = driver.putCreatureOnBattlefield(driver.player1, "Drover Grizzly")
        val other = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        driver.removeSummoningSickness(grizzly)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(driver.player1, listOf(grizzly), driver.player2)
        driver.bothPass()

        projector.hasProjectedKeyword(driver.state, grizzly, Keyword.TRAMPLE) shouldBe false
        projector.hasProjectedKeyword(driver.state, other, Keyword.TRAMPLE) shouldBe false
    }
})
