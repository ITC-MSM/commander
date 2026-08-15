package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.blb.cards.PlumecreedMentor
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Plumecreed Mentor:
 * {1}{W}{U} Creature — Bird Scout 2/3
 * Flying
 * Whenever this creature or another creature you control with flying enters,
 * put a +1/+1 counter on target creature you control without flying.
 */
class PlumecreedMentorTest : FunSpec({

    val allCards = TestCards.all + listOf(PlumecreedMentor)

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(allCards)
        return driver
    }

    test("trigger does NOT fire when a creature without flying enters") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Plains" to 20),
            startingLife = 20
        )

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val activePlayer = driver.activePlayer!!

        // Put Plumecreed Mentor on the battlefield (direct placement, no trigger)
        driver.putCreatureOnBattlefield(activePlayer, "Plumecreed Mentor")

        // Cast Glory Seeker (no flying) — should NOT trigger Plumecreed Mentor
        val glorySeekerCard = driver.putCardInHand(activePlayer, "Glory Seeker")
        driver.giveMana(activePlayer, Color.WHITE, 2)
        driver.castSpell(activePlayer, glorySeekerCard)
        driver.bothPass() // Resolve Glory Seeker

        // No trigger should have fired — no pending target selection decision
        driver.state.pendingDecision shouldBe null

        // Glory Seeker should have no counters
        val glorySeekerOnBf = driver.getCreatures(activePlayer)
            .first { driver.state.getEntity(it)!!.get<CardComponent>()!!.name == "Glory Seeker" }
        val counters = driver.state.getEntity(glorySeekerOnBf)?.get<CountersComponent>()
        val plusCounters = counters?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
        plusCounters shouldBe 0
    }

    test("trigger fires when another creature with flying enters") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Plains" to 20),
            startingLife = 20
        )

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val activePlayer = driver.activePlayer!!

        // Put Plumecreed Mentor on the battlefield (direct placement, no trigger)
        driver.putCreatureOnBattlefield(activePlayer, "Plumecreed Mentor")

        // Put Glory Seeker on battlefield (a creature without flying, valid target)
        val glorySeekerOnBf = driver.putCreatureOnBattlefield(activePlayer, "Glory Seeker")

        // Cast Birds of Paradise (has flying) — should trigger Plumecreed Mentor
        val birdsCard = driver.putCardInHand(activePlayer, "Birds of Paradise")
        driver.giveMana(activePlayer, Color.GREEN, 1)
        driver.castSpell(activePlayer, birdsCard)
        driver.bothPass() // Resolve Birds of Paradise

        // Trigger fires — select Glory Seeker as the target
        driver.submitTargetSelection(activePlayer, listOf(glorySeekerOnBf))

        // Resolve the triggered ability
        driver.bothPass()

        // Glory Seeker should now have a +1/+1 counter
        val counters = driver.state.getEntity(glorySeekerOnBf)?.get<CountersComponent>()
        val plusCounters = counters?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
        plusCounters shouldBe 1
    }
})
