package com.wingedsheep.gym.trainer.defaults

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.DecisionResponse
import com.wingedsheep.engine.core.PendingDecision
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.handlers.actions.decision.DecisionValidators
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.gym.trainer.spi.StructuredDecisionExpander
import com.wingedsheep.gym.trainer.spi.StructuredDecisionExpansion
import com.wingedsheep.sdk.model.EntityId

/**
 * Exact, policy-free expansion for structured families whose pending-decision metadata fully
 * describes a finite response space.
 *
 * A target decision with one requirement for at most one target is finite and described completely
 * by its pending-decision payload: one response per legal target, plus the empty selection when the
 * requirement is optional. Every candidate is filtered through [DecisionValidators] before it is
 * exposed. Variable-cardinality and multi-requirement target decisions remain unsupported because
 * current MCTS materializes every edge and has no caller-owned widening or response-budget policy.
 * Other structured families remain unsupported.
 *
 * Cancellation is deliberately not one of the responses. A
 * [com.wingedsheep.engine.core.CancelDecisionResponse] on a cast-time target decision rewinds to the
 * priority state that offered the cast, so a cancel edge is a transposition back to the search node's
 * own ancestor rather than an alternative *within* the decision — the "don't cast" branch already
 * exists where the cast was chosen. Declining an optional requirement is the empty selection, which
 * stays inside the decision.
 *
 * A supported decision left with no validator-approved response is reported as
 * [StructuredDecisionExpansion.Unsupported], not as an empty [StructuredDecisionExpansion.Complete]:
 * an empty complete set is unsearchable, so the caller's resolver fallback owns that degenerate case.
 */
object ExactStructuredDecisionExpander : StructuredDecisionExpander {

    override fun expand(
        state: GameState,
        decision: PendingDecision
    ): StructuredDecisionExpansion = when (decision) {
        is ChooseTargetsDecision -> {
            val requirement = decision.targetRequirements.singleOrNull()
            val legalTargets = requirement?.let { decision.legalTargets[it.index] }
            if (
                requirement == null || legalTargets == null ||
                requirement.maxTargets != 1 || requirement.minTargets !in 0..1
            ) {
                StructuredDecisionExpansion.Unsupported
            } else {
                complete(
                    state = state,
                    decision = decision,
                    candidates = targetResponses(
                        decision = decision,
                        requirementIndex = requirement.index,
                        legalTargets = legalTargets,
                        optional = requirement.minTargets == 0
                    )
                )
            }
        }

        else -> StructuredDecisionExpansion.Unsupported
    }

    private fun targetResponses(
        decision: ChooseTargetsDecision,
        requirementIndex: Int,
        legalTargets: List<EntityId>,
        optional: Boolean
    ): List<DecisionResponse> = buildList {
        if (optional) {
            add(TargetsResponse(decision.id, mapOf(requirementIndex to emptyList())))
        }

        for (target in legalTargets.distinct()) {
            add(TargetsResponse(decision.id, mapOf(requirementIndex to listOf(target))))
        }
    }

    private fun complete(
        state: GameState,
        decision: PendingDecision,
        candidates: List<DecisionResponse>
    ): StructuredDecisionExpansion {
        val legal = candidates.filter { DecisionValidators.validate(decision, it, state) == null }
        return if (legal.isEmpty()) {
            StructuredDecisionExpansion.Unsupported
        } else {
            StructuredDecisionExpansion.Complete(legal)
        }
    }
}
