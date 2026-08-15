package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.BurningSunCavalry
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Burning Sun Cavalry (LCI #138) — {1}{R} Creature — Human Knight 2/2.
 *
 * Oracle: "Whenever this creature attacks or blocks while you control a Dinosaur, this creature
 * gets +1/+1 until end of turn."
 *
 * "while you control a Dinosaur" is a trigger-time condition (NOT an intervening-if — CR 603.4
 * applies only to "if"): checked only as the cavalry attacks or blocks, not re-checked on resolution.
 *
 * Covered:
 *  1. Attacking while controlling a Dinosaur → cavalry gets +1/+1 (2/2 → 3/3) until end of turn.
 *  2. Attacking without a Dinosaur → no bonus; cavalry stays 2/2.
 *  3. Blocking while controlling a Dinosaur → cavalry gets +1/+1 (2/2 → 3/3) until end of turn.
 */
class BurningSunCavalryScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(BurningSunCavalry)
        return driver
    }

    fun GameTestDriver.power(id: EntityId): Int = state.projectedState.getPower(id) ?: 0
    fun GameTestDriver.toughness(id: EntityId): Int = state.projectedState.getToughness(id) ?: 0

    // ─────────────────────────────────────────────────────────────────────────
    // Test 1: Attacking while controlling a Dinosaur grants +1/+1 until end of turn
    // ─────────────────────────────────────────────────────────────────────────
    test("attacking while controlling a Dinosaur gives cavalry +1/+1 until end of turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)

        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)

        val cavalry = driver.putCreatureOnBattlefield(me, "Burning Sun Cavalry")
        driver.removeSummoningSickness(cavalry)
        // Ghalta, Primal Hunger is an Elder Dinosaur — satisfies "you control a Dinosaur"
        driver.putCreatureOnBattlefield(me, "Ghalta, Primal Hunger")

        driver.power(cavalry) shouldBe 2
        driver.toughness(cavalry) shouldBe 2

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(me, listOf(cavalry), opponent)

        // Resolve the attacks trigger — cavalry gets +1/+1
        driver.bothPass()

        driver.power(cavalry) shouldBe 3
        driver.toughness(cavalry) shouldBe 3
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Test 2: Attacking without a Dinosaur — trigger-time condition blocks the trigger
    // ─────────────────────────────────────────────────────────────────────────
    test("attacking without a Dinosaur does not give cavalry a bonus") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)

        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)

        val cavalry = driver.putCreatureOnBattlefield(me, "Burning Sun Cavalry")
        driver.removeSummoningSickness(cavalry)
        // No Dinosaur on our side

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(me, listOf(cavalry), opponent)

        // No trigger fires — the "while you control a Dinosaur" condition fails at trigger time
        driver.bothPass()

        driver.power(cavalry) shouldBe 2
        driver.toughness(cavalry) shouldBe 2
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Test 3: Blocking while controlling a Dinosaur grants +1/+1 until end of turn
    // ─────────────────────────────────────────────────────────────────────────
    test("blocking while controlling a Dinosaur gives cavalry +1/+1 until end of turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)

        val attacker = driver.activePlayer!!
        val cavalryPlayer = driver.getOpponent(attacker)

        // Active player has a creature to attack with
        val attackerCreature = driver.putCreatureOnBattlefield(attacker, "Grizzly Bears")
        driver.removeSummoningSickness(attackerCreature)

        // Opposing player has the cavalry and a Dinosaur to satisfy the condition
        val cavalry = driver.putCreatureOnBattlefield(cavalryPlayer, "Burning Sun Cavalry")
        driver.putCreatureOnBattlefield(cavalryPlayer, "Ghalta, Primal Hunger")

        driver.power(cavalry) shouldBe 2
        driver.toughness(cavalry) shouldBe 2

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(attackerCreature), cavalryPlayer)

        // Pass through any attack triggers (none here) → advance to declare blockers
        driver.bothPass()

        // Cavalry blocks the attacker — blocks trigger fires
        driver.declareBlockers(cavalryPlayer, mapOf(cavalry to listOf(attackerCreature)))

        // Resolve the blocks trigger — cavalry gets +1/+1
        driver.bothPass()

        driver.power(cavalry) shouldBe 3
        driver.toughness(cavalry) shouldBe 3
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Test 4: The "while" condition is trigger-time only, NOT intervening-if.
    // Once the trigger has fired with a Dinosaur in play, losing that Dinosaur
    // before resolution does NOT remove the ability — the +1/+1 still applies.
    // (Contrast an "if" clause, which CR 603.4 would re-check on resolution.)
    // ─────────────────────────────────────────────────────────────────────────
    test("Dinosaur leaving after the trigger fires still applies +1/+1 (no resolution re-check)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)

        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)

        val cavalry = driver.putCreatureOnBattlefield(me, "Burning Sun Cavalry")
        driver.removeSummoningSickness(cavalry)
        val dinosaur = driver.putCreatureOnBattlefield(me, "Ghalta, Primal Hunger")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(me, listOf(cavalry), opponent)

        // The trigger fired (Dinosaur was in play at declaration) and now sits on the stack.
        driver.stackSize shouldBe 1

        // Remove the only Dinosaur before the trigger resolves.
        driver.moveToGraveyard(dinosaur)

        // The trigger still resolves and pumps — "while" is not re-checked on resolution.
        driver.bothPass()

        driver.power(cavalry) shouldBe 3
        driver.toughness(cavalry) shouldBe 3
    }
})
