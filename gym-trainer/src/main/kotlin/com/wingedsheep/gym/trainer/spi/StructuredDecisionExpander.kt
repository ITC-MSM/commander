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
 * engine's authoritative decision validator in [state].
 */
fun interface StructuredDecisionExpander {
    fun expand(state: GameState, decision: PendingDecision): StructuredDecisionExpansion
}

/**
 * What an expander knows about a structured decision's response space.
 *
 * [Complete] is the finite set of legal semantic alternatives, one canonical response each. It is an
 * ordinary list rather than a lazy sequence: a caller materializes every edge anyway, and the type
 * admits no partial or bounded variant, so there is nothing for laziness to defer and no way for a
 * consumer to be surprised by a single-shot source. [Unsupported] means the expander makes no
 * completeness claim; callers may fall back to a strategic [StructuredDecisionResolver], but must not
 * describe that selected response as the complete legal response set.
 *
 * There is deliberately no partial result yet. A bounded or sampled source needs an explicit
 * caller-owned policy for how non-exhaustive branches affect search and training.
 *
 * A [Complete] holding no responses is unsearchable, so an expander that finds no legal response for
 * a family it otherwise supports reports [Unsupported] and lets the caller's resolver own the
 * degenerate case. [com.wingedsheep.gym.trainer.search.AlphaZeroSearch] rejects an empty [Complete]
 * at a live, non-terminal node rather than substituting a resolver choice for a response set that
 * claims to be exhaustive.
 */
sealed interface StructuredDecisionExpansion {
    data class Complete(val responses: List<DecisionResponse>) : StructuredDecisionExpansion

    data object Unsupported : StructuredDecisionExpansion
}
