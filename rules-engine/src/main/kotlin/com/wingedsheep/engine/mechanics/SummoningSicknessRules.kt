package com.wingedsheep.engine.mechanics

import com.wingedsheep.engine.mechanics.layers.ProjectedState
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.components.battlefield.SummoningSicknessComponent
import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.model.EntityId

/**
 * The **ability half** of summoning sickness (CR 302.6), in one place.
 *
 * CR 302.6 bundles two restrictions under one condition — "under its controller's control
 * continuously since their most recent turn began":
 *
 *  1. a creature's activated ability with `{T}` or `{Q}` in its cost can't be activated, and
 *  2. a creature can't attack.
 *
 * Haste (CR 702.10b/c) lifts both. [AbilityFlag.MAY_ACTIVATE_ABILITIES_AS_THOUGH_HASTY] — the
 * Thousand-Year Elixir / Shang-Chi permission, "you may activate abilities of creatures you control
 * as though those creatures had haste" — lifts **only (1)**.
 *
 * That asymmetry is why this object exists and why it answers *only* question (1). Combat keeps its
 * own check in `AttackRestrictionRules`, which reads [Keyword.HASTE] directly and never consults
 * this object, so no as-though-hasty grant can leak into attack legality.
 *
 * Callers keep their own "is this a creature / is this a land" guard — the land carve-out differs
 * between call sites (a `{T}` cost on the source itself exempts lands; a `TapAttachedCreature` cost
 * does not) and folding it in here would silently change behaviour at half the sites. This answers
 * exactly one question: *given that this permanent's `{T}`/`{Q}` cost is subject to CR 302.6, is it
 * blocked right now?*
 *
 * Every `{T}`/`{Q}` activation gate in the engine routes through here;
 * `SummoningSicknessGateEnforcementTest` fails the build if a new direct
 * [SummoningSicknessComponent] read appears outside the allowlisted files.
 */
object SummoningSicknessRules {

    /**
     * Whether summoning sickness currently blocks [entityId] from paying a `{T}` or `{Q}`
     * activation cost.
     *
     * [container] is [entityId]'s component container, which every call site already has in hand —
     * the map lookup is skipped so this stays free in the mana-solver and enumerator hot paths. The
     * as-though-hasty flag lookup is only reached for a permanent that is actually sick *and*
     * hasteless, which is rare.
     *
     * **Caller invariant:** [container] must be [entityId]'s *own* container. The pair is not
     * checked — passing a mismatched one reads the sickness marker off one permanent and haste off
     * another, and mis-gates silently. Note that the `TapAttachedCreature` sites correctly pass the
     * *attached creature's* id and container, not the Aura's.
     */
    fun blocksTapOrUntapCost(
        entityId: EntityId,
        container: ComponentContainer,
        projected: ProjectedState
    ): Boolean =
        container.has<SummoningSicknessComponent>() &&
            !projected.hasKeyword(entityId, Keyword.HASTE) &&
            !projected.hasKeyword(entityId, AbilityFlag.MAY_ACTIVATE_ABILITIES_AS_THOUGH_HASTY)
}
