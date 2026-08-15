package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.MalametVeteran
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Malamet Veteran (LCI #201) — {4}{G} Creature — Cat Warrior 5/4.
 *
 * "Trample
 *  Descend 4 — Whenever this creature attacks, if there are four or more permanent cards in your
 *  graveyard, put a +1/+1 counter on target creature."
 *
 * Covered end-to-end:
 *  1. Descend 4 met (four permanent cards in the graveyard): attacking fires the trigger and a
 *     +1/+1 counter is placed on the chosen target creature.
 *  2. Descend 4 not met (three permanent cards): the intervening-if fails, so attacking produces
 *     no trigger and no counter is placed.
 */
class MalametVeteranScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(MalametVeteran)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        return driver
    }

    /** Read +1/+1 counter count on a permanent. */
    fun GameTestDriver.plusOneCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.counters?.get(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("Descend 4 met: attacking puts a +1/+1 counter on the target creature") {
        val driver = newDriver()
        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        val veteran = driver.putCreatureOnBattlefield(attacker, "Malamet Veteran")
        driver.removeSummoningSickness(veteran)

        // A second creature to receive the counter (any creature is a legal target).
        val bear = driver.putCreatureOnBattlefield(attacker, "Grizzly Bears")

        // Four permanent cards in the graveyard — meets the Descend 4 threshold.
        repeat(4) { driver.putCardInGraveyard(attacker, "Forest") }

        driver.plusOneCounters(bear) shouldBe 0

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(veteran), defender)
        // The attack trigger goes on the stack; choose the counter target, then resolve.
        driver.submitTargetSelection(attacker, listOf(bear))
        driver.bothPass()

        driver.plusOneCounters(bear) shouldBe 1
    }

    test("Descend 4 not met: attacking with only three permanent cards places no counter") {
        val driver = newDriver()
        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        val veteran = driver.putCreatureOnBattlefield(attacker, "Malamet Veteran")
        driver.removeSummoningSickness(veteran)

        val bear = driver.putCreatureOnBattlefield(attacker, "Grizzly Bears")

        // Only three permanent cards — one short of Descend 4.
        repeat(3) { driver.putCardInGraveyard(attacker, "Forest") }

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(veteran), defender)
        driver.bothPass()

        // Intervening-if failed: no trigger, no counter on either creature.
        driver.plusOneCounters(bear) shouldBe 0
        driver.plusOneCounters(veteran) shouldBe 0
    }
})
