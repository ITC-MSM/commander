package com.wingedsheep.gym.trainer.defaults

import com.wingedsheep.engine.core.CancelDecisionResponse
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.DecisionContext
import com.wingedsheep.engine.core.OrderObjectsDecision
import com.wingedsheep.engine.core.TargetRequirementInfo
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.handlers.actions.decision.DecisionValidators
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.gym.trainer.spi.StructuredDecisionExpansion
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.asClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class ExactStructuredDecisionExpanderTest : FunSpec({

    val playerId = EntityId.of("player")
    val first = EntityId.of("first")
    val second = EntityId.of("second")
    val third = EntityId.of("third")
    val state = GameState(turnOrder = listOf(playerId))

    test("single-target expansion is complete") {
        val decision = ChooseTargetsDecision(
            id = "targets",
            playerId = playerId,
            prompt = "Choose targets",
            context = DecisionContext(),
            targetRequirements = listOf(
                TargetRequirementInfo(0, "one target", minTargets = 1, maxTargets = 1)
            ),
            legalTargets = mapOf(0 to listOf(first, second)),
            canCancel = true
        )

        val expansion = ExactStructuredDecisionExpander.expand(state, decision)
            .shouldBeInstanceOf<StructuredDecisionExpansion.Complete>()
        val responses = expansion.responses.toList()

        responses.shouldContainExactly(
            CancelDecisionResponse("targets"),
            TargetsResponse("targets", mapOf(0 to listOf(first))),
            TargetsResponse("targets", mapOf(0 to listOf(second)))
        )
        responses.forEach { response ->
            response.asClue {
                DecisionValidators.validate(decision, response, state) shouldBe null
            }
        }
    }

    test("variable-cardinality target expansion is explicitly unsupported") {
        val decision = ChooseTargetsDecision(
            id = "many-targets",
            playerId = playerId,
            prompt = "Choose targets",
            context = DecisionContext(),
            targetRequirements = listOf(
                TargetRequirementInfo(0, "up to two", minTargets = 0, maxTargets = 2)
            ),
            legalTargets = mapOf(0 to listOf(first, second, third))
        )

        ExactStructuredDecisionExpander.expand(state, decision) shouldBe
            StructuredDecisionExpansion.Unsupported
    }

    test("supported family with no legal response is a complete empty expansion") {
        val decision = ChooseTargetsDecision(
            id = "no-targets",
            playerId = playerId,
            prompt = "Choose a target",
            context = DecisionContext(),
            targetRequirements = listOf(
                TargetRequirementInfo(0, "one target", minTargets = 1, maxTargets = 1)
            ),
            legalTargets = mapOf(0 to emptyList())
        )

        val expansion = ExactStructuredDecisionExpander.expand(state, decision)
            .shouldBeInstanceOf<StructuredDecisionExpansion.Complete>()
        expansion.responses.toList() shouldBe emptyList()
    }

    test("unsupported family returns unsupported") {
        val decision = OrderObjectsDecision(
            id = "order",
            playerId = playerId,
            prompt = "Order objects",
            context = DecisionContext(),
            objects = listOf(first, second)
        )

        ExactStructuredDecisionExpander.expand(state, decision) shouldBe
            StructuredDecisionExpansion.Unsupported
    }
})
