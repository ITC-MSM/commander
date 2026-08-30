package com.wingedsheep.gym.trainer.spi

import com.wingedsheep.engine.core.DecisionResponse
import com.wingedsheep.engine.core.PendingDecision
import com.wingedsheep.engine.state.GameState

/**
 * Generates the legal response alternatives that a search may branch over for a structured
 * [PendingDecision]. This is a legality/enumeration seam, not a policy: implementations must not
 * select, sample, truncate, or rank responses on behalf of a search algorithm.
 *
 * Every response in a [StructuredDecisionExpansion.Complete] result must be accepted by the
 * engine's authoritative decision validator in [state]. The sequence may be lazy so an expander
 * does not have to materialize a combinatorial response space merely to describe it.
 */
fun interface StructuredDecisionExpander {
    fun expand(state: GameState, decision: PendingDecision): StructuredDecisionExpansion
}

/**
 * What an expander knows about a structured decision's response space.
 *
 * [Complete] contains a finite, possibly lazy sequence with one canonical response per legal
 * semantic alternative. It may contain zero, one, or many responses. [Unsupported] means the
 * expander makes no completeness claim; callers may fall back to a strategic
 * [StructuredDecisionResolver], but must not describe that selected response as the complete legal
 * response set.
 *
 * There is deliberately no partial result yet. A bounded or sampled source needs an explicit
 * caller-owned policy for how non-exhaustive branches affect search and training.
 * [com.wingedsheep.gym.trainer.search.AlphaZeroSearch] treats a complete empty result at a live,
 * non-terminal node as an engine-state invariant failure: it does not substitute a resolver choice
 * for a response set known to be empty.
 */
sealed interface StructuredDecisionExpansion {
    class Complete(val responses: Sequence<DecisionResponse>) : StructuredDecisionExpansion

    data object Unsupported : StructuredDecisionExpansion
}
