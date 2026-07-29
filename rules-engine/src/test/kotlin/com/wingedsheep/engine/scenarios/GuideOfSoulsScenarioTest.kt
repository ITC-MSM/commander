package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
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

/**
 * Guide of Souls (MH3) — proves the two new pieces the card needed: the player-scoped Energy
 * "get 1 on another creature ETB" trigger, and [com.wingedsheep.sdk.scripting.effects.PayFixedCountersEffect]
 * as the all-or-nothing action half of a reflexive "may pay {E}{E}{E}. When you do, ..." ability.
 */
class GuideOfSoulsScenarioTest : FunSpec({

    val projector = StateProjector()

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        return d
    }

    fun energyOf(d: GameTestDriver, playerId: EntityId): Int =
        d.state.getEntity(playerId)?.get<CountersComponent>()?.getCount(CounterType.ENERGY) ?: 0

    fun counterOf(d: GameTestDriver, entityId: EntityId, type: CounterType): Int =
        d.state.getEntity(entityId)?.get<CountersComponent>()?.getCount(type) ?: 0

    fun seedEnergy(d: GameTestDriver, playerId: EntityId, amount: Int) {
        d.replaceState(
            d.state.updateEntity(playerId) { container ->
                val current = container.get<CountersComponent>() ?: CountersComponent()
                container.with(current.withAdded(CounterType.ENERGY, amount))
            }
        )
    }

    /** Drain priority/triggers until a YesNoDecision surfaces (or we give up). */
    fun drainToYesNo(d: GameTestDriver, maxSteps: Int = 10): Boolean {
        repeat(maxSteps) {
            if (d.pendingDecision is YesNoDecision) return true
            if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass()
        }
        return d.pendingDecision is YesNoDecision
    }

    test("another creature entering under your control gains 1 life and 1 energy") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        val active = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCreatureOnBattlefield(active, "Guide of Souls")
        val lifeBefore = d.getLifeTotal(active)
        energyOf(d, active) shouldBe 0

        d.giveMana(active, Color.GREEN, 2) // Grizzly Bears costs {1}{G}
        val bearsId = d.putCardInHand(active, "Grizzly Bears")
        d.castSpell(active, bearsId)
        d.bothPass() // resolves Grizzly Bears, detects and queues the trigger
        d.bothPass() // resolves the queued triggered ability itself

        d.getLifeTotal(active) shouldBe lifeBefore + 1
        energyOf(d, active) shouldBe 1
    }

    test("paying {E}{E}{E} on attack puts 2 +1/+1 and a flying counter on the target and makes it an Angel") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        val active = d.activePlayer!!
        val opp = d.getOpponent(active)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCreatureOnBattlefield(active, "Guide of Souls")
        val wurm = d.putCreatureOnBattlefield(active, "Craw Wurm")
        d.removeSummoningSickness(wurm)
        seedEnergy(d, active, 3)

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(active, listOf(wurm), opp)

        drainToYesNo(d) shouldBe true
        d.submitYesNo(active, true)
        repeat(10) {
            if (d.pendingDecision != null && d.pendingDecision !is YesNoDecision) {
                d.submitTargetSelection(active, listOf(wurm))
            } else if (d.pendingDecision != null) {
                d.autoResolveDecision()
            } else {
                d.bothPass()
            }
        }

        val projected = projector.project(d.state)
        counterOf(d, wurm, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 2
        counterOf(d, wurm, CounterType.FLYING) shouldBe 1
        projected.hasSubtype(wurm, "Angel") shouldBe true
        energyOf(d, active) shouldBe 0
    }

    test("declining the may-pay leaves the attacker and energy untouched") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        val active = d.activePlayer!!
        val opp = d.getOpponent(active)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCreatureOnBattlefield(active, "Guide of Souls")
        val wurm = d.putCreatureOnBattlefield(active, "Craw Wurm")
        d.removeSummoningSickness(wurm)
        seedEnergy(d, active, 3)

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(active, listOf(wurm), opp)

        drainToYesNo(d) shouldBe true
        d.submitYesNo(active, false)
        repeat(6) { if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass() }

        val projected = projector.project(d.state)
        counterOf(d, wurm, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 0
        projected.hasSubtype(wurm, "Angel") shouldBe false
        energyOf(d, active) shouldBe 3
    }

    test("with fewer than 3 energy, the may-pay prompt never appears at all") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        val active = d.activePlayer!!
        val opp = d.getOpponent(active)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCreatureOnBattlefield(active, "Guide of Souls")
        val wurm = d.putCreatureOnBattlefield(active, "Craw Wurm")
        d.removeSummoningSickness(wurm)
        seedEnergy(d, active, 2)

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(active, listOf(wurm), opp)

        var sawMayPayPrompt = false
        repeat(10) {
            if (d.pendingDecision is YesNoDecision) sawMayPayPrompt = true
            if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass()
        }

        // The prompt must never appear at all — isActionFeasible gates it before offering the
        // "may pay" yes/no, not just before letting the payment go through.
        sawMayPayPrompt shouldBe false

        val projected = projector.project(d.state)
        counterOf(d, wurm, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 0
        projected.hasSubtype(wurm, "Angel") shouldBe false
        energyOf(d, active) shouldBe 2
    }
})
