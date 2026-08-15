package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tmt.cards.MikeyAndDonPartyPlanners
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Mikey & Don, Party Planners (TMT) — a creature cast from the top of your
 * library via Mikey & Don's permission enters with an additional +1/+1 counter.
 *
 * Exercises the new CastFromLibraryComponent: a spell cast with castFromZone == LIBRARY is
 * marked, and a selfOnly = false EntersWithCounters gated on WasCastFromZone(LIBRARY) puts the
 * counter on the entering creature.
 */
class MikeyAndDonScenarioTest : FunSpec({

    val testTurtle = card("Test Turtle") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Turtle"
        power = 2
        toughness = 2
    }

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + listOf(MikeyAndDonPartyPlanners, testTurtle))
    }

    fun GameTestDriver.resolveStack() {
        var guard = 0
        while (state.stack.isNotEmpty() && guard < 50) {
            if (state.pendingDecision != null) autoResolveDecision() else bothPass()
            guard++
        }
    }

    test("a Turtle cast from the top of the library via Mikey & Don enters with an extra +1/+1 counter") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val me = d.activePlayer!!

        d.putCreatureOnBattlefield(me, "Mikey & Don, Party Planners")
        val turtleId = d.putCardOnTopOfLibrary(me, "Test Turtle")

        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.giveMana(me, Color.GREEN, 2)

        val cast = d.castSpell(me, turtleId)
        cast.error shouldBe null
        d.resolveStack()

        val onBattlefield = d.findPermanent(me, "Test Turtle")!!
        val counters = d.state.getEntity(onBattlefield)?.get<CountersComponent>()
        (counters?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0) shouldBe 1
    }
})
