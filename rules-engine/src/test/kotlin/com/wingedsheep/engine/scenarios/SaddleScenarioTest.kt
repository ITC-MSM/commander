package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SaddleMount
import com.wingedsheep.engine.state.components.battlefield.SaddledComponent
import com.wingedsheep.engine.support.GameTestDriver
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
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * End-to-end scenario tests for the Saddle N keyword (CR 702.171).
 *
 * Test card: a 1/4 Mount with Saddle 2 and "Whenever this attacks while saddled, draw a card",
 * which exercises both the saddle special action and the "while saddled" trigger gate.
 */
class SaddleScenarioTest : FunSpec({

    val testMount = card("Test Mount") {
        manaCost = "{2}"
        typeLine = "Creature — Mount"
        power = 1
        toughness = 4
        oracleText = "Saddle 2\nWhenever Test Mount attacks while saddled, draw a card."
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

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(testMount)
        driver.registerCard(testPony)
        driver.initMirrorMatch(Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.isSaddled(id: EntityId): Boolean =
        state.getEntity(id)?.has<SaddledComponent>() == true

    test("saddling taps the chosen creatures and marks the Mount saddled (CR 702.171a/c)") {
        val driver = newDriver()
        val mount = driver.putCreatureOnBattlefield(driver.player1, "Test Mount")
        val bear = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears") // power 2

        driver.isSaddled(mount) shouldBe false

        driver.submitSuccess(SaddleMount(driver.player1, mount, listOf(bear)))
        driver.bothPass() // resolve the saddle ability off the stack

        driver.isTapped(bear) shouldBe true
        driver.isSaddled(mount) shouldBe true
    }

    test("Saddle 2 cannot be paid by a single power-1 creature") {
        val driver = newDriver()
        val mount = driver.putCreatureOnBattlefield(driver.player1, "Test Mount")
        val pony = driver.putCreatureOnBattlefield(driver.player1, "Test Pony") // power 1

        driver.submitExpectFailure(SaddleMount(driver.player1, mount, listOf(pony)))
        driver.isSaddled(mount) shouldBe false
    }

    test("a Mount cannot saddle itself (CR 702.171a: 'other' creatures)") {
        val driver = newDriver()
        val mount = driver.putCreatureOnBattlefield(driver.player1, "Test Mount")
        driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")

        driver.submitExpectFailure(SaddleMount(driver.player1, mount, listOf(mount)))
        driver.isSaddled(mount) shouldBe false
    }

    test("Saddle cannot be activated at instant speed (sorcery-speed only, CR 702.171a)") {
        val driver = newDriver()
        val mount = driver.putCreatureOnBattlefield(driver.player1, "Test Mount")
        val bear = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")

        // Move to a combat step (not a main phase): saddle must be rejected.
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.submitExpectFailure(SaddleMount(driver.player1, mount, listOf(bear)))
        driver.isSaddled(mount) shouldBe false
    }

    test("re-saddling an already-saddled Mount is legal (CR 702.171b)") {
        val driver = newDriver()
        val mount = driver.putCreatureOnBattlefield(driver.player1, "Test Mount")
        val bear1 = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        val bear2 = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")

        driver.submitSuccess(SaddleMount(driver.player1, mount, listOf(bear1)))
        driver.bothPass()
        driver.isSaddled(mount) shouldBe true

        // Saddle again with the other creature — still legal, still saddled.
        driver.submitSuccess(SaddleMount(driver.player1, mount, listOf(bear2)))
        driver.bothPass()
        driver.isSaddled(mount) shouldBe true
        driver.isTapped(bear2) shouldBe true
    }

    test("'attacks while saddled' trigger fires only when the Mount is saddled") {
        val driver = newDriver()
        val mount = driver.putCreatureOnBattlefield(driver.player1, "Test Mount")
        val bear = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        driver.removeSummoningSickness(mount)

        val handBefore = driver.getHandSize(driver.player1)

        driver.submitSuccess(SaddleMount(driver.player1, mount, listOf(bear)))
        driver.bothPass()
        driver.isSaddled(mount) shouldBe true

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(driver.player1, listOf(mount), driver.player2)
        driver.bothPass() // resolve the attack trigger

        // Saddled at attack time → drew a card.
        driver.getHandSize(driver.player1) shouldBe handBefore + 1
    }

    test("'attacks while saddled' trigger does NOT fire when the Mount is unsaddled") {
        val driver = newDriver()
        val mount = driver.putCreatureOnBattlefield(driver.player1, "Test Mount")
        driver.removeSummoningSickness(mount)

        val handBefore = driver.getHandSize(driver.player1)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(driver.player1, listOf(mount), driver.player2)
        driver.bothPass()

        // Never saddled → no card drawn.
        driver.getHandSize(driver.player1) shouldBe handBefore
    }

    test("saddled is cleared at end of turn (CR 702.171b)") {
        val driver = newDriver()
        val mount = driver.putCreatureOnBattlefield(driver.player1, "Test Mount")
        val bear = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")

        driver.submitSuccess(SaddleMount(driver.player1, mount, listOf(bear)))
        driver.bothPass()
        driver.isSaddled(mount) shouldBe true

        // Advance past player1's cleanup into the opponent's main phase. (We're already in
        // player1's precombat main, so step forward first, then to the next precombat main.)
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.isSaddled(mount) shouldBe false
    }
})
