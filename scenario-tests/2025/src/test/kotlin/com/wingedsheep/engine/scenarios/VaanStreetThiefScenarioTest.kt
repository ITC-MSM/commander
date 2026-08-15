package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.VaanStreetThief
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Vaan, Street Thief — {2}{R} Legendary Creature — Human Scout (2/2).
 *
 * 1. "Whenever one or more Scouts, Pirates, and/or Rogues you control deal combat damage to a
 *    player, exile the top card of that player's library. You may cast it. If you don't, create a
 *    Treasure token."
 * 2. "Whenever you cast a spell you don't own, put a +1/+1 counter on each Scout, Pirate, and Rogue
 *    you control."
 *
 * The first ability is a per-damaged-player combat batch — it exiles the top card of *that player's*
 * library (Player.TriggeringPlayer = the damaged player). Casting the exiled (opponent-owned) card is
 * "a spell you don't own", so it also triggers the second ability.
 */
class VaanStreetThiefScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + PredefinedTokens.allTokens + VaanStreetThief)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /** Put Vaan + a Mountain on my board, swing Vaan unblocked, and pause on the may-cast choice. */
    fun setupAndSwing(driver: GameTestDriver, me: EntityId, opp: EntityId): EntityId {
        val vaan = driver.putCreatureOnBattlefield(me, "Vaan, Street Thief")
        driver.removeSummoningSickness(vaan)
        driver.putPermanentOnBattlefield(me, "Mountain") // {R} for casting the exiled card

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(me, listOf(vaan), opp)
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareNoBlockers(opp)

        var safety = 0
        while (!driver.isPaused && safety++ < 30) driver.bothPass()
        return vaan
    }

    test("combat damage exiles the top of the DAMAGED player's library and casts it; second ability adds counters") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        // The damaged player's (opponent's) top card — a castable {R} creature.
        driver.putCardOnTopOfLibrary(opp, "Goblin Guide")

        val vaan = setupAndSwing(driver, me, opp)

        driver.isPaused shouldBe true
        // Card was exiled from the opponent's library (proves Player.TriggeringPlayer = damaged player).
        driver.getExileCardNames(opp).contains("Goblin Guide") shouldBe true

        // Choose to cast it (paying {R} from the Mountain via auto-pay).
        driver.submitYesNo(me, true)
        var safety = 0
        while (driver.isPaused && safety++ < 15) driver.bothPass()
        repeat(4) { driver.bothPass() }

        // The opponent-owned card is now a permanent under my control.
        driver.findPermanent(me, "Goblin Guide") shouldNotBe null
        // No Treasure was made (we cast it).
        driver.findPermanent(me, "Treasure") shouldBe null
        // "Cast a spell you don't own" put a +1/+1 counter on Vaan (a Scout I control).
        val counters = driver.state.getEntity(vaan)?.get<CountersComponent>()?.counters ?: emptyMap()
        counters[CounterType.PLUS_ONE_PLUS_ONE] shouldBe 1
    }

    test("declining the may-cast creates a Treasure and leaves the card exiled, with no counters added") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        driver.putCardOnTopOfLibrary(opp, "Goblin Guide")

        val vaan = setupAndSwing(driver, me, opp)

        driver.isPaused shouldBe true
        driver.getExileCardNames(opp).contains("Goblin Guide") shouldBe true

        // Decline the cast.
        driver.submitYesNo(me, false)
        var safety = 0
        while (driver.isPaused && safety++ < 15) driver.bothPass()
        repeat(2) { driver.bothPass() }

        // A Treasure was created, the card stays in the opponent's exile, and nothing was cast.
        driver.findPermanent(me, "Treasure") shouldNotBe null
        driver.getExileCardNames(opp).contains("Goblin Guide") shouldBe true
        driver.findPermanent(me, "Goblin Guide") shouldBe null
        val counters = driver.state.getEntity(vaan)?.get<CountersComponent>()?.counters ?: emptyMap()
        (counters[CounterType.PLUS_ONE_PLUS_ONE] ?: 0) shouldBe 0
    }
})
