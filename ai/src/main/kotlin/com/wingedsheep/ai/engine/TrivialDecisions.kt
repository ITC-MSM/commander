package com.wingedsheep.ai.engine

import com.wingedsheep.engine.core.AssignDamageDecision
import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.ChooseColorDecision
import com.wingedsheep.engine.core.ChooseModeDecision
import com.wingedsheep.engine.core.ChooseNumberDecision
import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.ColorChosenResponse
import com.wingedsheep.engine.core.DecisionResponse
import com.wingedsheep.engine.core.ModesChosenResponse
import com.wingedsheep.engine.core.NumberChosenResponse
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.OrderObjectsDecision
import com.wingedsheep.engine.core.OrderedResponse
import com.wingedsheep.engine.core.PendingDecision
import com.wingedsheep.engine.core.ReorderLibraryDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.core.TargetsResponse

/**
 * Decisions with exactly one legal answer.
 *
 * "Trivial" here means *forced*, not *easy*: a single legal target, a forced card selection, a mode
 * that is the only available one. Answering these needs no strategy and no simulation, so both the
 * strategic path ([GameSimulator], on the way to a quiet state) and the rollout path
 * ([com.wingedsheep.ai.engine.rollout.FastDecisionResponder], inside a playout) start here and only
 * fall through to their own policy when the choice is real.
 *
 * This helper is deliberately conservative. Defaults, solver suggestions, and decisions whose
 * uniqueness depends on state stay with the caller's policy; surfacing a forced decision is cheaper
 * than silently making a strategic choice here.
 */
object TrivialDecisions {

    /** The forced response to [decision], or null when the choice is a real one. */
    fun responseFor(decision: PendingDecision): DecisionResponse? = when (decision) {
        // A single requirement has one mandatory target, with no cancellation or state-dependent
        // cap. Cross-requirement distinctness is not represented on ChooseTargetsDecision, so a
        // multi-requirement response cannot be proved unique here.
        is ChooseTargetsDecision -> {
            val requirementIndices = decision.targetRequirements.map { it.index }
            val allForced = !decision.canCancel &&
                requirementIndices.size <= 1 &&
                requirementIndices.size == requirementIndices.toSet().size &&
                decision.targetRequirements.all { req ->
                    val targets = decision.legalTargets[req.index] ?: emptyList()
                    targets.size == 1 &&
                        req.minTargets == 1 &&
                        req.maxTargets == 1 &&
                        req.totalManaValueAtMost == null
                }
            if (allForced) {
                TargetsResponse(
                    decisionId = decision.id,
                    selectedTargets = decision.targetRequirements.associate { req ->
                        req.index to decision.legalTargets[req.index]!!
                    }
                )
            } else null
        }

        // The shared response contract intentionally permits repeated IDs for distributed counter
        // removal. Without metadata distinguishing that use, only zero/one-option selections can
        // prove uniqueness here; larger all-options selections stay policy-owned.
        is SelectCardsDecision -> {
            val hasStateDependentConstraints = decision.onePerCardType ||
                decision.onePerColor ||
                decision.onePerCardName ||
                decision.onePerBasicLandType ||
                decision.onePerPower ||
                decision.maxTotalManaValue != null ||
                decision.minTotalManaValue != null ||
                decision.maxTotalPower != null ||
                decision.conditionalMinimums.isNotEmpty()
            if (decision.options.size <= 1 &&
                !hasStateDependentConstraints &&
                decision.minSelections == decision.options.size &&
                decision.maxSelections == decision.options.size
            ) {
                CardsSelectedResponse(decision.id, decision.options)
            } else null
        }

        // A default assignment is an initial policy choice, not proof that no other distribution is
        // legal. The rollout responder may still confirm it as its own heuristic.
        is AssignDamageDecision -> null

        // autoPaySuggestion is one solver-selected payment. Manual source choices, mana abilities
        // with side effects, and (when offered) declining remain strategy-owned.
        is SelectManaSourcesDecision -> null

        // Single option with no separate cancel response.
        is ChooseOptionDecision -> {
            if (!decision.canCancel && decision.options.size == 1) {
                OptionChosenResponse(decision.id, 0)
            } else null
        }

        // Single color
        is ChooseColorDecision -> {
            if (decision.availableColors.size == 1) {
                ColorChosenResponse(decision.id, decision.availableColors.first())
            } else null
        }

        // Exact one-mode contract. A broader maximum is left conservative because the current
        // response contract does not independently describe repeatability.
        is ChooseModeDecision -> {
            val available = decision.modes.filter { it.available }
            if (available.size == 1 && decision.minModes == 1 && decision.maxModes == 1) {
                ModesChosenResponse(decision.id, listOf(available.first().index))
            } else null
        }

        // Number with single valid value
        is ChooseNumberDecision -> {
            if (decision.minValue == decision.maxValue) {
                NumberChosenResponse(decision.id, decision.minValue)
            } else null
        }

        // Single object ordering
        is OrderObjectsDecision -> {
            if (decision.objects.size <= 1) {
                OrderedResponse(decision.id, decision.objects)
            } else null
        }

        // Library reordering with single card
        is ReorderLibraryDecision -> {
            if (decision.cards.size <= 1) {
                OrderedResponse(decision.id, decision.cards)
            } else null
        }

        else -> null
    }
}
