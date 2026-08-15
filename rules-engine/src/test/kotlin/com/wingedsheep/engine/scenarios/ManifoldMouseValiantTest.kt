package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.OrderTriggeredAbilitiesDecision
import com.wingedsheep.engine.core.TriggeredAbilitiesOrderedResponse
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.blb.cards.ManifoldMouse
import com.wingedsheep.mtg.sets.definitions.blb.cards.NettleGuard
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Regression test: a triggered ability that targets a creature must emit a
 * BecomesTargetEvent that feeds trigger detection, so the targeted creature's
 * Valiant trigger fires.
 *
 * Setup: P1 controls Manifold Mouse and Nettle Guard. At beginning of combat,
 * Manifold Mouse's trigger targets Nettle Guard. Because Nettle Guard becomes
 * the target of an ability P1 controls for the first time this turn, its
 * Valiant ability should fire and grant +0/+2 until end of turn.
 */
class ManifoldMouseValiantTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(ManifoldMouse, NettleGuard))
        return driver
    }

    fun GameTestDriver.advanceToPlayer1BeginCombat() {
        passPriorityUntil(Step.BEGIN_COMBAT)
        var safety = 0
        while (activePlayer != player1 && safety < 50) {
            bothPass()
            passPriorityUntil(Step.BEGIN_COMBAT)
            safety++
        }
    }

    /**
     * Production asks the controller to order simultaneous triggered abilities.
     * This legacy test does not test their order, so it takes the stable detector
     * order before continuing with its target-selection assertion.
     */
    fun GameTestDriver.acceptDefaultTriggerOrderIfNeeded() {
        val order = pendingDecision as? OrderTriggeredAbilitiesDecision ?: return
        submitDecision(
            order.playerId,
            TriggeredAbilitiesOrderedResponse(order.id, order.abilities.map { it.id })
        )
    }

    test("Two Manifold Mouse triggers, first targets Nettle Guard — Valiant fires") {
        // Reproduces the bug from the Offspring token copy variant: when the original
        // Manifold Mouse and a token copy both have begin-combat triggers, the first
        // trigger pauses for target selection, queuing the second as PendingTriggersContinuation.
        //
        // After the user picks Nettle Guard as the first trigger's target,
        // putTriggeredAbility emits a BecomesTargetEvent. The PendingTriggersContinuation
        // auto-resumer immediately runs and pauses on the second trigger's target prompt.
        // Because the chain re-paused, SubmitDecisionHandler's success-path trigger detection
        // (line 108) never ran — and the auto-resumer never ran detectTriggers on the events.
        // Result: Valiant was lost and Nettle Guard stayed 3/1 even after the chain unpaused.
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))

        val nettleGuard = driver.putCreatureOnBattlefield(driver.player1, "Nettle Guard")
        val mouse1 = driver.putCreatureOnBattlefield(driver.player1, "Manifold Mouse")
        val mouse2 = driver.putCreatureOnBattlefield(driver.player1, "Manifold Mouse")
        driver.removeSummoningSickness(nettleGuard)
        driver.removeSummoningSickness(mouse1)
        driver.removeSummoningSickness(mouse2)

        driver.advanceToPlayer1BeginCombat()
        driver.acceptDefaultTriggerOrderIfNeeded()

        // First begin-combat trigger targets Nettle Guard — this is the BecomesTargetEvent
        // that should fire Valiant. The bug was that this event was dropped because the
        // chain re-paused on the second trigger's target prompt.
        driver.pendingDecision as ChooseTargetsDecision
        driver.submitTargetSelection(driver.player1, listOf(nettleGuard))
        driver.acceptDefaultTriggerOrderIfNeeded()

        // Second trigger asks for a target — pick Mouse #2 (different target so its
        // own BecomesTargetEvent doesn't accidentally re-fire Valiant via the working path).
        driver.pendingDecision as ChooseTargetsDecision
        driver.submitTargetSelection(driver.player1, listOf(mouse2))
        driver.acceptDefaultTriggerOrderIfNeeded()

        // Resolve until Valiant applies +0/+2. Target-placement continuations may leave a Mouse
        // modal trigger above it, so service that choice without making stack adjacency part of
        // this regression's contract.
        var resolutionGuard = 0
        while (projector.project(driver.state).getToughness(nettleGuard) != 3 && resolutionGuard < 10) {
            resolutionGuard++
            driver.acceptDefaultTriggerOrderIfNeeded()
            when (val decision = driver.pendingDecision) {
                is ChooseOptionDecision -> driver.submitDecision(
                    decision.playerId,
                    OptionChosenResponse(decision.id, 0)
                )
                null -> if (driver.getTopOfStack() != null) {
                    driver.bothPass()
                } else {
                    error("Valiant did not reach the stack after both Mouse targets were selected")
                }
                else -> error("unexpected decision while resolving Valiant: $decision")
            }
        }

        val afterValiant = projector.project(driver.state)
        afterValiant.getToughness(nettleGuard) shouldBe 3
    }

    test("Manifold Mouse targeting Nettle Guard triggers Valiant +0/+2") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))

        val nettleGuard = driver.putCreatureOnBattlefield(driver.player1, "Nettle Guard")
        driver.putCreatureOnBattlefield(driver.player1, "Manifold Mouse")
        driver.removeSummoningSickness(nettleGuard)

        driver.advanceToPlayer1BeginCombat()
        driver.acceptDefaultTriggerOrderIfNeeded()

        // Baseline: Nettle Guard is 3/1.
        val baseline = projector.project(driver.state)
        baseline.getPower(nettleGuard) shouldBe 3
        baseline.getToughness(nettleGuard) shouldBe 1

        // Manifold Mouse begin-combat trigger asks for a target.
        driver.pendingDecision as ChooseTargetsDecision
        driver.submitTargetSelection(driver.player1, listOf(nettleGuard))
        driver.acceptDefaultTriggerOrderIfNeeded()

        // After targeting, both Manifold Mouse's trigger and Nettle Guard's
        // Valiant trigger are on the stack. Resolve Valiant first (top of stack).
        driver.bothPass()
        driver.acceptDefaultTriggerOrderIfNeeded()

        // Valiant has resolved → Nettle Guard is 3/3.
        val afterValiant = projector.project(driver.state)
        afterValiant.getPower(nettleGuard) shouldBe 3
        afterValiant.getToughness(nettleGuard) shouldBe 3

        // The Mouse trigger remains independently resolvable on the stack. This regression
        // concerns the Valiant event and its trigger placement, already asserted above.
    }
})
