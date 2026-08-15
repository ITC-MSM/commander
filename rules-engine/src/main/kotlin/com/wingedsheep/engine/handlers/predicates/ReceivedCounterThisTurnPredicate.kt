package com.wingedsheep.engine.handlers.predicates

import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.components.battlefield.ReceivedCountersThisTurnComponent
import com.wingedsheep.sdk.scripting.predicates.StatePredicate

/**
 * Whether the permanent held by [container] satisfies
 * [StatePredicate.ReceivedCounterThisTurn] — "one or more counters were put on it this turn",
 * optionally scoped to a counter kind and to placements made by the permanent's own controller.
 *
 * The single read behind every dispatch site (target/gather filtering, group-static projection,
 * trigger gating, untap filtering) and behind `Conditions.SourceReceivedCounterThisTurn`, which is
 * `SourceMatches` over the same predicate. It answers purely from the per-permanent
 * [ReceivedCountersThisTurnComponent] marker — no projection, no source context — so all four
 * sites can give the same answer.
 *
 * The marker is stamped at placement time and cleared at end-of-turn cleanup, so the predicate
 * keeps matching after the counters themselves have been removed, which is what the printed
 * wording asks ("what you *put on* it this turn", not "what is on it now").
 */
fun receivedCounterThisTurn(
    container: ComponentContainer,
    predicate: StatePredicate.ReceivedCounterThisTurn
): Boolean {
    val marker = container.get<ReceivedCountersThisTurnComponent>() ?: return false
    // Both axes read the same way: pick the set the placer axis selects, then either look for one
    // kind in it or ask whether anything landed at all. `recordCounterPlacement` requires a counter
    // kind, so a recorded placement always populates `counterTypes` — which is what lets the
    // any-kind reading be `isNotEmpty()` rather than a separate marker-presence shortcut. A
    // shortcut there would have made the two any-kind readings disagree: placer-agnostic would
    // answer from the marker existing, while "you've put" still had to consult a type set.
    val recorded = if (predicate.placedByController) marker.typesFromController else marker.counterTypes
    return predicate.counterType?.let { it in recorded } ?: recorded.isNotEmpty()
}
