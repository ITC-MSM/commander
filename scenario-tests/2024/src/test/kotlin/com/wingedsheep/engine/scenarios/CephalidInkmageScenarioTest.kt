package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.DeclareBlockers
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fdn.cards.CephalidInkmage
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainIgnoringCase

/**
 * Cephalid Inkmage — {2}{U} Octopus Wizard 2/2
 *
 * "Threshold — This creature can't be blocked as long as there are seven or more cards in your
 * graveyard."
 *
 * Proves the graveyard-count-gated `CantBeBlocked` static: with 7+ cards in the controller's
 * graveyard the attacker can't be blocked; with fewer it can.
 */
class CephalidInkmageScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(CephalidInkmage))
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun seedGraveyard(driver: GameTestDriver, count: Int) {
        repeat(count) { driver.putCardInGraveyard(driver.player1, "Island") }
    }

    test("with 7+ cards in graveyard, Cephalid Inkmage can't be blocked") {
        val driver = createDriver()
        seedGraveyard(driver, 7)

        val inkmage = driver.putCreatureOnBattlefield(driver.player1, "Cephalid Inkmage")
        driver.removeSummoningSickness(inkmage)
        val blocker = driver.putCreatureOnBattlefield(driver.player2, "Centaur Courser")
        driver.removeSummoningSickness(blocker)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(driver.player1, listOf(inkmage), driver.player2).error shouldBe null
        driver.bothPass()
        driver.currentStep shouldBe Step.DECLARE_BLOCKERS

        val result = driver.submitExpectFailure(
            DeclareBlockers(driver.player2, mapOf(blocker to listOf(inkmage)))
        )
        result.isSuccess shouldBe false
        result.error shouldContainIgnoringCase "can't be blocked"
    }

    test("with fewer than 7 cards in graveyard, Cephalid Inkmage can be blocked") {
        val driver = createDriver()
        seedGraveyard(driver, 6)

        val inkmage = driver.putCreatureOnBattlefield(driver.player1, "Cephalid Inkmage")
        driver.removeSummoningSickness(inkmage)
        val blocker = driver.putCreatureOnBattlefield(driver.player2, "Centaur Courser")
        driver.removeSummoningSickness(blocker)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(driver.player1, listOf(inkmage), driver.player2).error shouldBe null
        driver.bothPass()
        driver.currentStep shouldBe Step.DECLARE_BLOCKERS

        driver.declareBlockers(
            driver.player2, mapOf(blocker to listOf(inkmage))
        ).isSuccess shouldBe true
    }
})
