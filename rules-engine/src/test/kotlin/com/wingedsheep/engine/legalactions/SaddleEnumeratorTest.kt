package com.wingedsheep.engine.legalactions

import com.wingedsheep.engine.legalactions.support.EnumerationTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.KeywordAbility
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

/**
 * Tests for [enumerators.SaddleEnumerator] (CR 702.171a) using an inline Mount with Saddle 2.
 *
 * Saddle reuses the Crew "tap creatures with total power N" selection, but adds the
 * sorcery-speed restriction and excludes the Mount itself from the eligible creatures.
 */
class SaddleEnumeratorTest : FunSpec({

    val testMount = card("Test Mount") {
        manaCost = "{2}"
        typeLine = "Creature — Mount"
        power = 1
        toughness = 4
        oracleText = "Saddle 2"
        keywordAbility(KeywordAbility.saddle(2))
        triggeredAbility {
            trigger = Triggers.Attacks
            triggerRestriction = Conditions.SourceIsSaddled
            effect = Effects.DrawCards(1)
        }
    }

    val testPony = card("Test Pony") {
        manaCost = "{1}"
        typeLine = "Creature — Horse"
        power = 1
        toughness = 1
        oracleText = ""
    }

    fun driverInMainPhase(): EnumerationTestDriver {
        val driver = EnumerationTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCards(listOf(testMount, testPony))
        driver.game.initMirrorMatch(Deck.of("Forest" to 40), skipMulligans = true)
        driver.game.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun EnumerationTestDriver.saddleActionsFor(playerId: EntityId) =
        enumerateFor(playerId).filter { it.actionType == "SaddleMount" }

    test("a Mount with Saddle surfaces a SaddleMount action at sorcery speed") {
        val driver = driverInMainPhase()
        driver.game.putCreatureOnBattlefield(driver.player1, "Test Mount")
        driver.game.putCreatureOnBattlefield(driver.player1, "Grizzly Bears") // 2/2

        val saddleActions = driver.saddleActionsFor(driver.player1)

        saddleActions shouldHaveSize 1
        val saddle = saddleActions.single()
        saddle.tapForPower shouldBe true
        saddle.tapForPowerRequired shouldBe 2
        saddle.affordable shouldBe true // Grizzly Bears' 2 power meets Saddle 2
    }

    test("Saddle 2 is unaffordable when only a single power-1 creature is available") {
        val driver = driverInMainPhase()
        driver.game.putCreatureOnBattlefield(driver.player1, "Test Mount")
        driver.game.putCreatureOnBattlefield(driver.player1, "Test Pony") // 1/1

        val saddle = driver.saddleActionsFor(driver.player1).single()
        saddle.tapForPowerRequired shouldBe 2
        saddle.tapForPowerCreatures!!.sumOf { it.power } shouldBe 1
        saddle.affordable shouldBe false
    }

    test("two power-1 creatures together can afford Saddle 2") {
        val driver = driverInMainPhase()
        driver.game.putCreatureOnBattlefield(driver.player1, "Test Mount")
        driver.game.putCreatureOnBattlefield(driver.player1, "Test Pony")
        driver.game.putCreatureOnBattlefield(driver.player1, "Test Pony")

        val saddle = driver.saddleActionsFor(driver.player1).single()
        saddle.tapForPowerCreatures!! shouldHaveSize 2
        saddle.affordable shouldBe true
    }

    test("the Mount itself is excluded from its own eligible saddle creatures") {
        val driver = driverInMainPhase()
        val mountId = driver.game.putCreatureOnBattlefield(driver.player1, "Test Mount")
        driver.game.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")

        val saddle = driver.saddleActionsFor(driver.player1).single()
        saddle.tapForPowerCreatures!!.map { it.entityId }.contains(mountId) shouldBe false
    }

    test("tapped creatures are not eligible to saddle") {
        val driver = driverInMainPhase()
        driver.game.putCreatureOnBattlefield(driver.player1, "Test Mount")
        val bear = driver.game.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        driver.game.tapPermanent(bear)

        val saddle = driver.saddleActionsFor(driver.player1).single()
        saddle.tapForPowerCreatures!!.map { it.entityId }.contains(bear) shouldBe false
        saddle.affordable shouldBe false // no untapped creature left to reach power 2
    }

    test("Saddle is NOT available to a player who isn't the active player (sorcery-speed gate)") {
        val driver = driverInMainPhase()
        // Mount controlled by player2 while it is player1's main phase.
        driver.game.putCreatureOnBattlefield(driver.player2, "Test Mount")
        driver.game.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")

        driver.saddleActionsFor(driver.player2).shouldBeEmpty()
    }
})
