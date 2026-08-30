package com.wingedsheep.ai.engine

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.DecisionContext
import com.wingedsheep.engine.core.DecisionPhase
import com.wingedsheep.engine.core.TargetRequirementInfo
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.handlers.actions.decision.DecisionValidators
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull

/**
 * The multi-requirement target responder scores one requirement at a time by simulating a candidate
 * response. Those probes have to be *complete* answers to the decision: `validateTargets` rejects a
 * response that leaves a mandatory requirement out, a rejected probe comes back as
 * `SimulationResult.Illegal` carrying the unchanged state, and every candidate then scores
 * identically — the pick silently collapses to "the first legal target", and the optional
 * pick-vs-skip comparison ties and always skips.
 *
 * So the property under test is the probe's *shape*, checked against the validator that judges it.
 */
class DecisionResponderTargetProbeTest : FunSpec({

    val player = EntityId.of("player")
    val creature = EntityId.of("creature")
    val other = EntityId.of("other")

    val responder = DecisionResponder(GameSimulator(CardRegistry()), AIPlayer.defaultEvaluator())

    val decision = ChooseTargetsDecision(
        id = "two-requirements",
        playerId = player,
        prompt = "Choose targets",
        context = DecisionContext(phase = DecisionPhase.RESOLUTION),
        targetRequirements = listOf(
            TargetRequirementInfo(index = 0, description = "target creature"),
            TargetRequirementInfo(index = 1, description = "up to one target creature", minTargets = 0),
        ),
        legalTargets = mapOf(0 to listOf(creature), 1 to listOf(other)),
    )

    test("a probe varying one requirement still answers the others") {
        val baseline = responder.minimalCompleteSelection(decision)

        // What the responder simulates for each candidate of requirement 1 — including the "pick
        // nothing" probe that decides whether an optional slot is worth filling.
        DecisionValidators.validate(
            decision,
            TargetsResponse(decision.id, baseline + (1 to listOf(other))),
        ).shouldBeNull()

        DecisionValidators.validate(
            decision,
            TargetsResponse(decision.id, baseline + (1 to emptyList())),
        ).shouldBeNull()
    }

    test("varying a requirement on its own is not a submittable response") {
        // The shape the probes used to have. It names only legal targets, so nothing about it is
        // wrong except that requirement 0 goes unanswered.
        DecisionValidators.validate(
            decision,
            TargetsResponse(decision.id, mapOf(1 to listOf(other))),
        ).shouldNotBeNull()
    }
})
