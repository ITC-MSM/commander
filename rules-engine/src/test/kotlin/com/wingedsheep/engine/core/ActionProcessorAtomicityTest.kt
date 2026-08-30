package com.wingedsheep.engine.core

import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.scripting.effects.Effect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.SuccessCriterion
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class ActionProcessorAtomicityTest : ScenarioTestBase() {
    init {
        test("a later nested continuation error rejects the whole submitted decision") {
            val (game, decision) = gameAwaitingDividedDamage(
                then = Effects.GainLife(1, EffectTarget.ContextTarget(0))
            )
            val preActionState = game.state

            val processed = actionProcessor.process(
                preActionState,
                SubmitDecision(
                    playerId = game.player1Id,
                    response = DistributionResponse(
                        decisionId = decision.id,
                        distribution = linkedMapOf(game.player2Id to 1)
                    )
                )
            )
            val result = processed.result

            result.error shouldBe "No valid target for life gain"
            assertSoftly {
                result.state shouldBe preActionState
                result.state.lifeTotal(game.player2Id) shouldBe 20
                result.state.pendingDecision shouldBe decision
                result.state.continuationStack shouldBe preActionState.continuationStack
                result.events.shouldBeEmpty()
                result.pendingDecision shouldBe null
                result.triggersAlreadyProcessed shouldBe false
                processed.undoPolicy shouldBe UndoCheckpointAction.PRESERVE
            }
        }

        test("a successful nested continuation commits its state and events") {
            val (game, decision) = gameAwaitingDividedDamage(
                then = Effects.GainLife(1, EffectTarget.Controller)
            )
            val preActionState = game.state

            val result = game.submitDecision(
                DistributionResponse(decision.id, linkedMapOf(game.player2Id to 1))
            )

            result.error shouldBe null
            result.pendingDecision shouldBe null
            result.state.lifeTotal(game.player1Id) shouldBe 21
            result.state.lifeTotal(game.player2Id) shouldBe 19
            result.state.pendingDecision shouldBe null
            result.state.continuationStack.shouldBeEmpty()
            result.events.shouldNotBeEmpty()
            result.events.filterIsInstance<LifeChangedEvent>().map { it.playerId to it.newLife } shouldBe
                listOf(game.player2Id to 19, game.player1Id to 21)
        }

        test("a nested continuation that pauses keeps its in-flight state and events") {
            val (game, decision) = gameAwaitingDividedDamage(
                then = MayEffect(Effects.GainLife(1, EffectTarget.Controller))
            )
            val preActionState = game.state

            val result = game.submitDecision(
                DistributionResponse(decision.id, linkedMapOf(game.player2Id to 1))
            )

            result.error shouldBe null
            result.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
            result.state.lifeTotal(game.player1Id) shouldBe 20
            result.state.lifeTotal(game.player2Id) shouldBe 19
            result.state.pendingDecision shouldBe result.pendingDecision
            result.state.continuationStack.shouldNotBeEmpty()
            result.events.shouldNotBeEmpty()
            result.events.filterIsInstance<LifeChangedEvent>().map { it.playerId to it.newLife } shouldBe
                listOf(game.player2Id to 19)
        }
    }

    private fun gameAwaitingDividedDamage(
        then: Effect
    ): Pair<ScenarioTestBase.TestGame, DistributeDecision> {
        val game = scenario().withPlayers().build()
        val decisionId = "divide-damage"
        val gatedFollowUp = GatedActionContinuation(
            decisionId = "evaluate-damage",
            then = then,
            otherwise = null,
            successCriterion = SuccessCriterion.Always,
            snapshot = GatedActionSnapshot(),
            effectContext = EffectContext(
                sourceId = null,
                controllerId = game.player1Id,
                targets = emptyList()
            )
        )
        val damage = DistributeDamageContinuation(
            decisionId = decisionId,
            sourceId = null,
            controllerId = game.player1Id,
            targets = listOf(game.player2Id)
        )
        val decision = DistributeDecision(
            id = decisionId,
            playerId = game.player1Id,
            prompt = "Divide 1 damage",
            context = DecisionContext(),
            totalAmount = 1,
            targets = listOf(game.player2Id),
            minPerTarget = 1
        )
        game.state = game.state
            .pushContinuation(gatedFollowUp)
            .pushContinuation(damage)
            .withPendingDecision(decision)
        return game to decision
    }
}
