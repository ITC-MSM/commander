package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.OptionChosenResponse
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

        // First begin-combat trigger targets Nettle Guard — this is the BecomesTargetEvent
        // that should fire Valiant. The bug was that this event was dropped because the
        // chain re-paused on the second trigger's target prompt.
        driver.pendingDecision as ChooseTargetsDecision
        driver.submitTargetSelection(driver.player1, listOf(nettleGuard))

        // Second trigger asks for a target — pick Mouse #2 (different target so its
        // own BecomesTargetEvent doesn't accidentally re-fire Valiant via the working path).
        driver.pendingDecision as ChooseTargetsDecision
        driver.submitTargetSelection(driver.player1, listOf(mouse2))

        // Resolve the stack — Valiant should be on top, +0/+2 applied.
        driver.bothPass()

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

        // Baseline: Nettle Guard is 3/1.
        val baseline = projector.project(driver.state)
        baseline.getPower(nettleGuard) shouldBe 3
        baseline.getToughness(nettleGuard) shouldBe 1

        // Manifold Mouse begin-combat trigger asks for a target.
        driver.pendingDecision as ChooseTargetsDecision
        driver.submitTargetSelection(driver.player1, listOf(nettleGuard))

        // After targeting, both Manifold Mouse's trigger and Nettle Guard's
        // Valiant trigger are on the stack. Resolve Valiant first (top of stack).
        driver.bothPass()

        // Valiant has resolved → Nettle Guard is 3/3.
        val afterValiant = projector.project(driver.state)
        afterValiant.getPower(nettleGuard) shouldBe 3
        afterValiant.getToughness(nettleGuard) shouldBe 3

        // Now resolve the Manifold Mouse trigger; it presents the mode choice.
        driver.bothPass()
        val modeDecision = driver.pendingDecision as ChooseOptionDecision
        driver.submitDecision(driver.player1, OptionChosenResponse(modeDecision.id, 0))

        // Toughness from Valiant should still be applied (until end of turn).
        val afterMode = projector.project(driver.state)
        afterMode.getToughness(nettleGuard) shouldBe 3
    }
})
