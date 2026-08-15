package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.PugnaciousHammerskull
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Pugnacious Hammerskull (LCI #208) — {2}{G} Creature — Dinosaur 6/6.
 *
 * Oracle: "Whenever this creature attacks while you don't control another Dinosaur, put a stun
 * counter on it. (If a permanent with a stun counter would become untapped, remove one from it
 * instead.)"
 *
 * "while you don't control another Dinosaur" is a negated, self-excluding intervening-if
 * condition (CR 603.4): the Hammerskull's own Dinosaur type is excluded, and the check is checked
 * both when the trigger would fire and again when it tries to resolve.
 *
 * Covered:
 *  1. Attacking as the only Dinosaur → one stun counter is placed on it.
 *  2. Attacking while controlling another Dinosaur → no trigger; no stun counter.
 */
class PugnaciousHammerskullScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(PugnaciousHammerskull)
        return driver
    }

    fun GameTestDriver.stunCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.STUN) ?: 0

    // ─────────────────────────────────────────────────────────────────────────
    // Test 1: Attacking as the only Dinosaur places a stun counter on itself
    // ─────────────────────────────────────────────────────────────────────────
    test("attacking without another Dinosaur puts a stun counter on the Hammerskull") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)

        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)

        val hammerskull = driver.putCreatureOnBattlefield(me, "Pugnacious Hammerskull")
        driver.removeSummoningSickness(hammerskull)
        // No other Dinosaur on our side (the Hammerskull's own type is excluded).

        driver.stunCounters(hammerskull) shouldBe 0

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(me, listOf(hammerskull), opponent)

        // Resolve the attacks trigger — one stun counter is placed.
        driver.bothPass()

        driver.stunCounters(hammerskull) shouldBe 1
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Test 2: Controlling another Dinosaur suppresses the trigger (intervening-if)
    // ─────────────────────────────────────────────────────────────────────────
    test("attacking while controlling another Dinosaur places no stun counter") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)

        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)

        val hammerskull = driver.putCreatureOnBattlefield(me, "Pugnacious Hammerskull")
        driver.removeSummoningSickness(hammerskull)
        // Ghalta, Primal Hunger is an Elder Dinosaur — "another Dinosaur" you control.
        driver.putCreatureOnBattlefield(me, "Ghalta, Primal Hunger")

        driver.stunCounters(hammerskull) shouldBe 0

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(me, listOf(hammerskull), opponent)

        // The intervening-if fails — no trigger fires, no stun counter.
        driver.bothPass()

        driver.stunCounters(hammerskull) shouldBe 0
    }
})
