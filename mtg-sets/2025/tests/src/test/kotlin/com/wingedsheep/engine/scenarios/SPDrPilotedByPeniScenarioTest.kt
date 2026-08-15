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
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * SP//dr, Piloted by Peni (SPM) — {3}{W}{U} Legendary Artifact Creature 4/4.
 *
 *  "Vigilance
 *   When SP//dr enters, put a +1/+1 counter on target creature.
 *   Whenever a modified creature you control deals combat damage to a player, draw a card."
 *
 * Two independent pieces to prove:
 *  1. The ETB trigger targets *any* creature (including an opponent's) and puts a counter on it.
 *  2. The combat-damage trigger only fires for a *modified* creature you control — an unmodified
 *     creature dealing combat damage to a player does not draw a card, but a modified one does
 *     (whether the modification is SP//dr's own counter, or a counter placed on another creature).
 */
class SPDrPilotedByPeniScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        return driver
    }

    /** Cast SP//dr from hand, paying its {3}{W}{U} cost from a freshly-filled mana pool. */
    fun GameTestDriver.castSPDr(playerId: EntityId): EntityId {
        val spdrCard = putCardInHand(playerId, "SP//dr, Piloted by Peni")
        giveColorlessMana(playerId, 3)
        giveMana(playerId, Color.WHITE, 1)
        giveMana(playerId, Color.BLUE, 1)
        castSpell(playerId, spdrCard).isSuccess shouldBe true
        bothPass() // resolve the creature spell itself; the ETB trigger goes on the stack next
        return findPermanent(playerId, "SP//dr, Piloted by Peni")!!
    }

    /** Fully resolve the stack, resolving every triggered ability that lands on it. */
    fun GameTestDriver.resolveStack() {
        var guard = 0
        while (state.stack.isNotEmpty() && guard < 50) {
            bothPass()
            guard++
        }
    }

    /** Pass priority until a target-selection decision appears (or the stack empties). */
    fun GameTestDriver.advanceToTargetDecision() {
        var guard = 0
        while (guard++ < 20) {
            if (pendingDecision is ChooseTargetsDecision) return
            if (state.stack.isNotEmpty() || state.priorityPlayerId != null) bothPass() else break
        }
    }

    fun GameTestDriver.advanceToOwnDeclareAttackers(owner: EntityId) {
        passPriorityUntil(Step.DECLARE_ATTACKERS)
        var safety = 0
        while (activePlayer != owner && safety < 50) {
            bothPass()
            passPriorityUntil(Step.DECLARE_ATTACKERS)
            safety++
        }
    }

    test("ETB trigger puts a +1/+1 counter on target creature, including an opponent's") {
        val driver = createDriver()
        val p1 = driver.player1
        val p2 = driver.player2
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val opposingBear = driver.putCreatureOnBattlefield(p2, "Grizzly Bears")

        driver.castSPDr(p1)

        // The creature spell resolves, then the ETB trigger goes on the stack and resolving
        // it prompts target selection.
        driver.advanceToTargetDecision()
        driver.pendingDecision.shouldBeInstanceOf<ChooseTargetsDecision>()

        val decision = driver.pendingDecision as ChooseTargetsDecision
        val legalTargets = decision.legalTargets.values.flatten().toSet()
        legalTargets.contains(opposingBear) shouldBe true

        driver.submitTargetSelection(p1, listOf(opposingBear))
        driver.resolveStack()

        driver.state.getEntity(opposingBear)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
    }

    test("only a modified creature dealing combat damage to a player draws a card") {
        val driver = createDriver()
        val p1 = driver.player1
        val p2 = driver.player2
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val spdr = driver.castSPDr(p1)
        driver.advanceToTargetDecision()
        // Put the ETB counter on SP//dr itself, making it modified.
        driver.submitTargetSelection(p1, listOf(spdr))
        driver.resolveStack()
        driver.removeSummoningSickness(spdr)

        // An unmodified vanilla creature that will also attack.
        val plainBear = driver.putCreatureOnBattlefield(p1, "Grizzly Bears")
        driver.removeSummoningSickness(plainBear)

        driver.advanceToOwnDeclareAttackers(p1)
        val handSizeBeforeCombat = driver.getHandSize(p1)

        driver.declareAttackers(p1, listOf(spdr, plainBear), p2)

        // Vigilance: SP//dr attacked without tapping; the vanilla bear (no vigilance) is tapped.
        driver.isTapped(spdr) shouldBe false
        driver.isTapped(plainBear) shouldBe true

        // Actually advance through declare blockers + the combat damage step (resolveStack()
        // alone is a no-op here — right after declaring attackers there's nothing on the stack
        // yet; the trigger only appears once damage is dealt).
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        // SP//dr (modified, +1/+1 counter) draws a card; the plain bear does not.
        driver.getHandSize(p1) shouldBe handSizeBeforeCombat + 1
    }

    test("a creature that becomes modified via an unrelated counter also triggers the draw") {
        val driver = createDriver()
        val p1 = driver.player1
        val p2 = driver.player2
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Create the bear *before* casting SP//dr so it's already on the battlefield (and thus a
        // legal target) by the time the ETB trigger's target decision is generated.
        val bear = driver.putCreatureOnBattlefield(p1, "Grizzly Bears")

        driver.castSPDr(p1)
        driver.advanceToTargetDecision()

        // Send the ETB counter onto the bear instead, making it modified.
        driver.submitTargetSelection(p1, listOf(bear))
        driver.resolveStack()
        driver.removeSummoningSickness(bear)

        driver.advanceToOwnDeclareAttackers(p1)
        val handSizeBeforeCombat = driver.getHandSize(p1)

        driver.declareAttackers(p1, listOf(bear), p2)
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        driver.getHandSize(p1) shouldBe handSizeBeforeCombat + 1
    }
})
