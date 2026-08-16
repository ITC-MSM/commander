package com.wingedsheep.engine.handlers.predicates

import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.components.battlefield.HasBecomeTappedComponent
import com.wingedsheep.sdk.scripting.predicates.StatePredicate

/**
 * Whether the permanent held by [container] satisfies
 * [StatePredicate.BecameTappedOnlyOnceThisTurn] — it has become tapped exactly once so far during
 * [currentTurn].
 *
 * The single read behind every dispatch site (target/gather filtering, group-static projection,
 * trigger gating, untap-step filtering), so they cannot drift. It answers purely from the
 * per-permanent [HasBecomeTappedComponent] counter plus [currentTurn] — no projection, no source
 * context.
 *
 * A marker stamped on an earlier turn counts as zero taps this turn, which is what lets the window
 * expire without an end-of-turn cleanup entry. No marker at all is likewise zero: a permanent that
 * entered the battlefield tapped never *became* tapped (CR 701.26a), so it is not "the first time"
 * for anything, and neither is a permanent nobody has tapped.
 *
 * @param currentTurn `state.turnNumber`.
 */
fun becameTappedOnlyOnceThisTurn(container: ComponentContainer, currentTurn: Int): Boolean {
    val marker = container.get<HasBecomeTappedComponent>() ?: return false
    return marker.lastBecameTappedTurn == currentTurn && marker.timesThisTurn == 1
}
