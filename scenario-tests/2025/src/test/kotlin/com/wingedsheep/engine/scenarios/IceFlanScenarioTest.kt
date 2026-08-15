package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Ice Flan (FIN #55).
 *
 * Ice Flan {4}{U}{U} Creature — Elemental Ooze 5/4
 * When this creature enters, tap target artifact or creature an opponent controls.
 * Put a stun counter on it.
 * Islandcycling {2}
 */
class IceFlanScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    fun stunCounters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.STUN) ?: 0

    test("ETB taps an opponent's creature and puts a stun counter on it") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val victim = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        val flan = driver.putCardInHand(active, "Ice Flan")
        driver.giveMana(active, Color.BLUE, 2)
        driver.giveColorlessMana(active, 4)

        driver.castSpell(active, flan).isSuccess shouldBe true
        driver.bothPass() // resolve the creature; ETB trigger goes on the stack and asks for a target

        val decision = driver.pendingDecision
        (decision is ChooseTargetsDecision) shouldBe true
        driver.submitTargetSelection(active, listOf(victim))
        driver.bothPass() // resolve the ETB trigger

        driver.isTapped(victim) shouldBe true
        stunCounters(driver, victim) shouldBe 1
    }
})
