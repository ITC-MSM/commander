package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.CrewVehicle
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Subterranean Schooner (LCI #80, {1}{U} Artifact — Vehicle 3/4).
 *
 * "Whenever this Vehicle attacks, target creature that crewed it this turn explores.
 *  (Reveal the top card of your library. Put that card into your hand if it's a land.
 *   Otherwise, put a +1/+1 counter on that creature, then put the card back or put it
 *   into your graveyard.)
 *  Crew 1"
 *
 * Exercises:
 *  1. The attack trigger only offers the creature that crewed the Schooner this turn as a
 *     legal target (source-relative `crewedOrSaddledSourceThisTurn` filter), and a land
 *     reveal goes to that player's hand with no counter.
 *  2. A nonland reveal puts a +1/+1 counter on the crewer (the exploring creature) and lets
 *     the player send the card to the graveyard.
 *
 * Subterranean Schooner is auto-discovered via [TestCards.all]; no explicit registration
 * is required.
 */
class SubterraneanSchoonerScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.plusOneCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("attack trigger only targets the crewer; a land reveal goes to hand with no counter") {
        val driver = newDriver()
        val schooner = driver.putPermanentOnBattlefield(driver.player1, "Subterranean Schooner")
        val crewer = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears") // power 2 >= Crew 1
        val bystander = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        driver.removeSummoningSickness(schooner)

        // Ensure the explored top card is a land.
        driver.putCardOnTopOfLibrary(driver.player1, "Forest")

        driver.submitSuccess(CrewVehicle(driver.player1, schooner, listOf(crewer)))
        driver.bothPass()

        val handBefore = driver.getHandSize(driver.player1)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(driver.player1, listOf(schooner), driver.player2)

        // The attack trigger asks for its target; only the crewer qualifies.
        val decision = driver.state.pendingDecision as ChooseTargetsDecision
        decision.legalTargets.getValue(0) shouldContain crewer
        decision.legalTargets.getValue(0) shouldNotContain bystander

        driver.submitTargetSelection(driver.player1, listOf(crewer))
        driver.bothPass()

        // Land reveal → into hand, no +1/+1 counter, no pending decision.
        driver.getHandSize(driver.player1) shouldBe handBefore + 1
        driver.plusOneCounters(crewer) shouldBe 0
        driver.state.pendingDecision shouldBe null
    }

    test("a nonland reveal puts a +1/+1 counter on the crewer and may go to the graveyard") {
        val driver = newDriver()
        val schooner = driver.putPermanentOnBattlefield(driver.player1, "Subterranean Schooner")
        val crewer = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        driver.removeSummoningSickness(schooner)

        // Ensure the explored top card is a nonland.
        driver.putCardOnTopOfLibrary(driver.player1, "Grizzly Bears")

        driver.submitSuccess(CrewVehicle(driver.player1, schooner, listOf(crewer)))
        driver.bothPass()

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(driver.player1, listOf(schooner), driver.player2)

        driver.submitTargetSelection(driver.player1, listOf(crewer))
        driver.bothPass()

        // Nonland reveal → counter on the crewer + pause for the back-or-graveyard choice.
        driver.plusOneCounters(crewer) shouldBe 1
        (driver.state.pendingDecision != null) shouldBe true

        // Choose graveyard (No = graveyard).
        driver.submitYesNo(driver.player1, false)

        driver.getGraveyardCardNames(driver.player1) shouldContain "Grizzly Bears"
        driver.plusOneCounters(crewer) shouldBe 1
    }
})
