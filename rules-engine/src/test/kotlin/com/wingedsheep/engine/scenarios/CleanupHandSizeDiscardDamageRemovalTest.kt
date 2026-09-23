package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.ActiveFloatingEffect
import com.wingedsheep.engine.mechanics.layers.FloatingEffectData
import com.wingedsheep.engine.mechanics.layers.Layer
import com.wingedsheep.engine.mechanics.layers.SerializableModification
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.Duration
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import com.wingedsheep.engine.core.Outcome

/**
 * Regression: marked damage must be removed at the cleanup step (CR 514.2) even when the
 * active player has to discard down to maximum hand size (CR 514.1) in the same step.
 *
 * The hand-size discard is a player decision, so
 * [com.wingedsheep.engine.core.CleanupPhaseManager.performCleanupStep] early-returns to ask for it
 * *before* it reaches the 514.2 damage-removal block. Previously the discard's continuation only
 * discarded and resumed — it never finished 514.2 — so marked damage survived into the next turn.
 * With deathtouch damage that an expiring "until end of turn" indestructible (Saved by the Shell)
 * had been suppressing, the creature then died on the following turn's state-based-action check,
 * with no destroy/sacrifice event.
 *
 * Fixed by extracting the 514.2 actions into
 * [com.wingedsheep.engine.core.CleanupPhaseManager.applyCleanupTurnBasedActions] and invoking it
 * from both the normal cleanup path and `resumeHandSizeDiscard`.
 */
class CleanupHandSizeDiscardDamageRemovalTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    /** Grant indestructible until end of turn (the relevant half of Saved by the Shell). */
    fun GameTestDriver.grantIndestructibleUntilEndOfTurn(entityId: EntityId, controllerId: EntityId) {
        val floatingEffect = ActiveFloatingEffect(
            id = EntityId.generate(),
            effect = FloatingEffectData(
                layer = Layer.ABILITY,
                modification = SerializableModification.GrantKeyword(Keyword.INDESTRUCTIBLE.name),
                affectedEntities = setOf(entityId)
            ),
            duration = Duration.EndOfTurn,
            sourceId = null,
            controllerId = controllerId,
            timestamp = System.currentTimeMillis()
        )
        replaceState(state.copy(floatingEffects = state.floatingEffects + floatingEffect))
    }

    /** Pad [playerId]'s hand well past the maximum hand size so cleanup forces a discard. */
    fun GameTestDriver.padHand(playerId: EntityId, cardName: String, count: Int) {
        repeat(count) { putCardInHand(playerId, cardName) }
    }

    test("deathtouch damage is cleared at cleanup despite a hand-size discard — creature survives next turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 30, "Swamp" to 10), startingLife = 20)

        val attackerController = driver.activePlayer!!
        val opponent = driver.getOpponent(attackerController)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // 3/3 vanilla attacker, granted indestructible until end of turn (Saved by the Shell).
        val attacker = driver.putCreatureOnBattlefield(attackerController, "Centaur Courser")
        driver.removeSummoningSickness(attacker)
        driver.grantIndestructibleUntilEndOfTurn(attacker, attackerController)

        // 1/1 deathtouch blocker — any damage it deals is lethal.
        val blocker = driver.putCreatureOnBattlefield(opponent, "Deathtouch Rat")

        // Push the attacking player over maximum hand size so the cleanup step forces a discard.
        driver.padHand(attackerController, "Forest", 8)

        // Attack into the deathtouch blocker; the attacker takes 1 deathtouch damage. Let
        // passPriorityUntil auto-resolve the combat-damage assignment.
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attackerController, listOf(attacker), opponent).outcome shouldBe Outcome.Done
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(opponent, mapOf(blocker to listOf(attacker))).outcome shouldBe Outcome.Done
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        // Indestructible kept the attacker alive through the deathtouch damage this turn.
        driver.findPermanent(attackerController, "Centaur Courser") shouldNotBe null

        // Advance into the opponent's upkeep — through this turn's cleanup (forcing the discard,
        // which passPriorityUntil auto-resolves) and into the next turn, where state-based actions
        // are checked. By then indestructible has worn off, so the only thing keeping the
        // now-mortal attacker alive is that its deathtouch damage was removed at cleanup.
        driver.passPriorityUntil(Step.UPKEEP)

        driver.activePlayer shouldBe opponent
        driver.findPermanent(attackerController, "Centaur Courser") shouldNotBe null
        driver.state.getEntity(attacker)?.has<DamageComponent>() shouldBe false
    }

    test("ordinary marked damage is removed at cleanup even when a hand-size discard interrupts the step") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 30, "Swamp" to 10), startingLife = 20)

        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val creature = driver.putCreatureOnBattlefield(player, "Centaur Courser") // 3/3
        driver.addComponent(creature, DamageComponent(amount = 2))
        driver.padHand(player, "Forest", 8)

        // Advance through this turn's cleanup (which forces a discard) into the opponent's upkeep.
        // Targeting UPKEEP (a step with a priority window) rather than the transient UNTAP step.
        driver.passPriorityUntil(Step.UPKEEP)

        // CR 514.2 still ran after the discard: the marked damage is gone.
        driver.activePlayer shouldBe opponent
        driver.state.getEntity(creature)?.has<DamageComponent>() shouldBe false
    }
})
