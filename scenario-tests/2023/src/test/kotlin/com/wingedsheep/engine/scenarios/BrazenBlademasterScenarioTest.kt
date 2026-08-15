package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.BrazenBlademaster
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Brazen Blademaster (LCI #136) — {2}{R} Creature — Orc Pirate 2/3.
 *
 * "Whenever this creature attacks while you control two or more artifacts,
 *  it gets +2/+1 until end of turn."
 *
 * Covered:
 *  1. Attacking while controlling two artifacts fires the trigger →
 *     Blademaster goes from 2/3 to 4/4 for the rest of the turn.
 *  2. Attacking while controlling only one artifact does NOT fire the trigger
 *     (the "while you control two or more artifacts" trigger-time condition fails) → stats remain 2/3.
 */
class BrazenBlademasterScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(BrazenBlademaster)
        return driver
    }

    fun GameTestDriver.power(id: EntityId): Int = state.projectedState.getPower(id) ?: 0
    fun GameTestDriver.toughness(id: EntityId): Int = state.projectedState.getToughness(id) ?: 0

    /** Advance to the Declare Attackers step on player 1's turn. */
    fun GameTestDriver.advanceToPlayer1DeclareAttackers() {
        passPriorityUntil(Step.DECLARE_ATTACKERS)
        var safety = 0
        while (activePlayer != player1 && safety < 50) {
            bothPass()
            passPriorityUntil(Step.DECLARE_ATTACKERS)
            safety++
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Test 1: Two artifacts in play → attack trigger fires → +2/+1 applied
    // ─────────────────────────────────────────────────────────────────────────
    test("attacking with two or more artifacts triggers the bonus, Blademaster becomes 4/4") {
        val driver = newDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val player = driver.player1

        val blademaster = driver.putCreatureOnBattlefield(player, "Brazen Blademaster")
        driver.removeSummoningSickness(blademaster)

        // Two Artifact Creatures satisfy "you control two or more artifacts".
        driver.putCreatureOnBattlefield(player, "Artifact Creature")
        driver.putCreatureOnBattlefield(player, "Artifact Creature")

        driver.power(blademaster) shouldBe 2
        driver.toughness(blademaster) shouldBe 3

        driver.advanceToPlayer1DeclareAttackers()
        driver.declareAttackers(driver.player1, listOf(blademaster), driver.player2)

        // The trigger-time condition is satisfied: trigger goes on stack and resolves.
        driver.bothPass()

        driver.power(blademaster) shouldBe 4
        driver.toughness(blademaster) shouldBe 4
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Test 2: Fewer than two artifacts → trigger-time condition fails → no trigger
    // ─────────────────────────────────────────────────────────────────────────
    test("attacking with only one artifact does not fire the trigger, stats stay 2/3") {
        val driver = newDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val player = driver.player1

        val blademaster = driver.putCreatureOnBattlefield(player, "Brazen Blademaster")
        driver.removeSummoningSickness(blademaster)

        // Only one artifact — the "two or more" threshold is not met.
        driver.putCreatureOnBattlefield(player, "Artifact Creature")

        driver.power(blademaster) shouldBe 2
        driver.toughness(blademaster) shouldBe 3

        driver.advanceToPlayer1DeclareAttackers()
        driver.declareAttackers(driver.player1, listOf(blademaster), driver.player2)

        // No trigger fires; pass through the post-attack-declaration priority window.
        driver.bothPass()

        driver.power(blademaster) shouldBe 2
        driver.toughness(blademaster) shouldBe 3
    }
})
