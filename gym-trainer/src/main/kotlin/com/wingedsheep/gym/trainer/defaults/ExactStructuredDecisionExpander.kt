package com.wingedsheep.gym.trainer.defaults

import com.wingedsheep.engine.core.CancelDecisionResponse
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
 * A target decision with exactly one requirement for exactly one target is finite and described
 * completely by its pending-decision payload. Every candidate is filtered through
 * [DecisionValidators] before it is exposed. Variable-cardinality and multi-requirement target
 * decisions remain unsupported because current MCTS materializes every edge and has no
 * caller-owned widening or response-budget policy. Other structured families remain unsupported.
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
                requirement.minTargets != 1 || requirement.maxTargets != 1
            ) {
                StructuredDecisionExpansion.Unsupported
            } else {
                complete(state, decision, targetResponses(decision, requirement.index, legalTargets))
            }
        }

        else -> StructuredDecisionExpansion.Unsupported
    }

    private fun targetResponses(
        decision: ChooseTargetsDecision,
        requirementIndex: Int,
        legalTargets: List<EntityId>
    ): Sequence<DecisionResponse> =
        sequence {
            if (decision.canCancel) {
                yield(CancelDecisionResponse(decision.id))
            }

            for (target in legalTargets.distinct()) {
                yield(
                    TargetsResponse(
                        decisionId = decision.id,
                        selectedTargets = mapOf(requirementIndex to listOf(target))
                    )
                )
            }
        }

    private fun complete(
        state: GameState,
        decision: PendingDecision,
        candidates: Sequence<DecisionResponse>
    ): StructuredDecisionExpansion.Complete = StructuredDecisionExpansion.Complete(
        candidates.filter { DecisionValidators.validate(decision, it, state) == null }
    )
}
