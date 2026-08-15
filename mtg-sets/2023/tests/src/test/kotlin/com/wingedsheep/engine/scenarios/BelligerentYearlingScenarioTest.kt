package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.BelligerentYearling
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Belligerent Yearling (LCI #133) — {1}{R} Creature — Dinosaur 3/2.
 *
 * "Trample
 *  Whenever another Dinosaur you control enters, you may have this creature's base power become
 *  equal to that creature's power until end of turn."
 *
 * Covered:
 *  1. Another Dinosaur entering lets the controller accept "you may": Yearling's base power is
 *     set to the entering Dinosaur's power for the rest of the turn (Layer 7b).
 *  2. The controller declines "you may": Yearling's power stays unchanged.
 *  3. A non-Dinosaur creature entering does NOT fire the trigger: Yearling's power is unchanged
 *     and no decision is presented.
 */
class BelligerentYearlingScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(BelligerentYearling)
        return driver
    }

    fun GameTestDriver.power(id: EntityId): Int = state.projectedState.getPower(id) ?: 0

    // ─────────────────────────────────────────────────────────────────────────
    // Test 1: Another Dinosaur enters → accept "you may" → Yearling's base power changes
    // ─────────────────────────────────────────────────────────────────────────
    test("another Dinosaur entering and accepting may sets Yearling's base power to that creature's power") {
        val driver = newDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val player = driver.player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Belligerent Yearling starts as a 3/2.
        val yearling = driver.putCreatureOnBattlefield(player, "Belligerent Yearling")
        driver.power(yearling) shouldBe 3

        // Cast Rampaging Ceratops ({4}{R}, 5/4 Dinosaur — no ETB trigger).
        // When it enters the battlefield, Yearling's trigger fires.
        val ceratops = driver.putCardInHand(player, "Rampaging Ceratops")
        driver.giveMana(player, Color.RED, 5)
        driver.castSpell(player, ceratops)

        // Ceratops resolves, then Yearling's trigger goes on the stack. Keep passing until the
        // MayEffect surfaces its yes/no pause (spell resolution + trigger resolution).
        var guard = 0
        while (!driver.isPaused && guard++ < 20) driver.bothPass()

        // Accept: Yearling's base power becomes Rampaging Ceratops' power (5) until end of turn.
        driver.submitYesNo(player, true)

        driver.power(yearling) shouldBe 5
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Test 2: Another Dinosaur enters → decline "you may" → Yearling's power unchanged
    // ─────────────────────────────────────────────────────────────────────────
    test("declining the may option leaves Yearling's base power unchanged") {
        val driver = newDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val player = driver.player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val yearling = driver.putCreatureOnBattlefield(player, "Belligerent Yearling")
        driver.power(yearling) shouldBe 3

        // Cast the same Dinosaur — trigger fires.
        val ceratops = driver.putCardInHand(player, "Rampaging Ceratops")
        driver.giveMana(player, Color.RED, 5)
        driver.castSpell(player, ceratops)

        // Pass until the MayEffect surfaces its yes/no pause.
        var guard = 0
        while (!driver.isPaused && guard++ < 20) driver.bothPass()

        // Decline: Yearling's base power stays at 3.
        driver.submitYesNo(player, false)

        driver.power(yearling) shouldBe 3
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Test 3: Non-Dinosaur entering does NOT trigger Yearling's ability
    // ─────────────────────────────────────────────────────────────────────────
    test("a non-Dinosaur creature entering does not trigger Yearling") {
        val driver = newDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val player = driver.player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val yearling = driver.putCreatureOnBattlefield(player, "Belligerent Yearling")
        driver.power(yearling) shouldBe 3

        // Cast Grizzly Bears ({G}{G}, 2/2 — not a Dinosaur): trigger must NOT fire.
        val bears = driver.putCardInHand(player, "Grizzly Bears")
        driver.giveMana(player, Color.GREEN, 2)
        driver.castSpell(player, bears)

        // Bears resolves and enters; no pending decision (trigger never fired).
        driver.bothPass()

        // Yearling's power is still 3 — no trigger, no change.
        driver.power(yearling) shouldBe 3
    }
})
