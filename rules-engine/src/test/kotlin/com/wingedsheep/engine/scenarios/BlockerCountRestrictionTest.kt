package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.DeclareBlockers
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainIgnoringCase
import com.wingedsheep.engine.core.Outcome
import io.kotest.matchers.shouldNotBe

/**
 * Tests for the `CantBeBlockedByMoreThan` static ability (CR 509.1b).
 *
 * Cards like Safewright Cavalry, Charging Rhino, and Stalking Tiger have the ability:
 * "This creature can't be blocked by more than one creature."
 *
 * Single-blocker assignments are legal; gang-blocking with 2+ creatures must be rejected.
 */
class BlockerCountRestrictionTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(
            deck = Deck.of(
                "Forest" to 20,
                "Plains" to 20,
                "Safewright Cavalry" to 4,
                "Grizzly Bears" to 4,
                "Savannah Lions" to 4
            ),
            skipMulligans = true
        )
        return driver
    }

    fun GameTestDriver.advanceToPlayer1DeclareAttackers() {
        passPriorityUntil(Step.DECLARE_ATTACKERS)
        var safety = 0
        while (activePlayer != player1 && safety < 50) {
            bothPass()
            passPriorityUntil(Step.DECLARE_ATTACKERS)
            safety++
        }
    }

    test("Safewright Cavalry can't be blocked by two creatures") {
        val driver = createDriver()

        val cavalry = driver.putCreatureOnBattlefield(driver.player1, "Safewright Cavalry")
        driver.removeSummoningSickness(cavalry)

        val bears = driver.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")
        val lions = driver.putCreatureOnBattlefield(driver.player2, "Savannah Lions")
        driver.removeSummoningSickness(bears)
        driver.removeSummoningSickness(lions)

        driver.advanceToPlayer1DeclareAttackers()
        driver.currentStep shouldBe Step.DECLARE_ATTACKERS
        driver.activePlayer shouldBe driver.player1

        driver.declareAttackers(driver.player1, listOf(cavalry), driver.player2)
            .outcome shouldBe Outcome.Done

        driver.bothPass()
        driver.currentStep shouldBe Step.DECLARE_BLOCKERS

        val result = driver.submitExpectFailure(
            DeclareBlockers(
                driver.player2,
                mapOf(
                    bears to listOf(cavalry),
                    lions to listOf(cavalry)
                )
            )
        )

        result.outcome shouldNotBe Outcome.Done
        result.error shouldContainIgnoringCase "more than one"
    }

    test("Safewright Cavalry can be blocked by a single creature") {
        val driver = createDriver()

        val cavalry = driver.putCreatureOnBattlefield(driver.player1, "Safewright Cavalry")
        driver.removeSummoningSickness(cavalry)

        val bears = driver.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")
        driver.removeSummoningSickness(bears)

        driver.advanceToPlayer1DeclareAttackers()
        driver.currentStep shouldBe Step.DECLARE_ATTACKERS

        driver.declareAttackers(driver.player1, listOf(cavalry), driver.player2)
            .outcome shouldBe Outcome.Done

        driver.bothPass()
        driver.currentStep shouldBe Step.DECLARE_BLOCKERS

        driver.declareBlockers(
            driver.player2,
            mapOf(bears to listOf(cavalry))
        ).outcome shouldBe Outcome.Done
    }

})
