package com.wingedsheep.engine.mechanics.cost

import com.wingedsheep.engine.handlers.PredicateContext
import com.wingedsheep.engine.handlers.PredicateEvaluator
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.costs.CostAtom
import com.wingedsheep.sdk.scripting.costs.PermanentCostAction
import com.wingedsheep.sdk.scripting.costs.VariableCostMeasure

/**
 * The one place that answers "which permanents can pay a [CostAtom.VariablePermanents] cost, and
 * how much do the chosen ones measure" — shared by every reader of that atom so the enumerator, the
 * validator, the payer, and the built-in AI can never disagree about affordability.
 *
 * The `TAP` + `TOTAL_POWER` shape is the one Teamwork N uses (CR 702.194a): "tap any number of
 * creatures you control with total power N or more" — the same selection crew (CR 702.122b) and
 * saddle already make, which is why [candidates] mirrors
 * `com.wingedsheep.engine.legalactions.enumerators.CrewEnumerator`: untapped, controlled by the
 * payer, matched through **projected** state, and with no summoning-sickness check (CR 302.6
 * governs the `{T}` symbol in an activation cost, not a tap paid as a cost).
 */
object VariablePermanentsCost {

    private val predicateEvaluator = PredicateEvaluator()

    /** Lower-case verb for this action, used in payment-failure messages and prompts. */
    fun verb(action: PermanentCostAction): String = when (action) {
        PermanentCostAction.EXILE -> "exile"
        PermanentCostAction.SACRIFICE -> "sacrifice"
        PermanentCostAction.TAP -> "tap"
    }

    /** Human name of the measured quantity, for "falls short of the required total power" errors. */
    fun measureName(measure: VariableCostMeasure): String = when (measure) {
        VariableCostMeasure.TOTAL_MANA_VALUE -> "total mana value"
        VariableCostMeasure.COUNT -> "count"
        VariableCostMeasure.TOTAL_POWER -> "total power"
    }

    /**
     * Measure [chosen] the way [measure] says — the value a `minMeasure` floor is compared against,
     * and the ability's X when it resolves (CR 601.2b).
     *
     * `TOTAL_MANA_VALUE` and `COUNT` read correctly even after the chosen permanents have left the
     * battlefield (mana value is intrinsic to the card; the count is a property of the selection).
     * `TOTAL_POWER` reads **projected** power, so a lord bonus or a +1/+1 counter counts toward a
     * teamwork threshold — and its permanents are only tapped, never moved, so they are still on
     * the battlefield when it is read.
     */
    fun measure(state: GameState, measure: VariableCostMeasure, chosen: List<EntityId>): Int =
        when (measure) {
            VariableCostMeasure.TOTAL_MANA_VALUE ->
                chosen.sumOf { state.getEntity(it)?.get<CardComponent>()?.manaValue ?: 0 }
            VariableCostMeasure.COUNT -> chosen.size
            VariableCostMeasure.TOTAL_POWER -> {
                val projected = state.projectedState
                chosen.sumOf { projected.getPower(it) ?: 0 }
            }
        }

    /**
     * Permanents [playerId] may choose to pay [atom], in battlefield order.
     *
     * A `TAP` atom sees only untapped permanents (CR 701.26a — "only untapped permanents can be
     * tapped"); the other actions see every match. [sourceId] is the cost's own source, excluded
     * when the atom sets `excludeSelf`; pass null for a spell's additional cost, which has no
     * source permanent on the battlefield.
     */
    fun candidates(
        state: GameState,
        playerId: EntityId,
        atom: CostAtom.VariablePermanents,
        sourceId: EntityId? = null,
    ): List<EntityId> {
        val projected = state.projectedState
        val context = PredicateContext(controllerId = playerId)
        return projected.getBattlefieldControlledBy(playerId).filter { entityId ->
            if (atom.excludeSelf && entityId == sourceId) return@filter false
            val container = state.getEntity(entityId) ?: return@filter false
            container.get<CardComponent>() ?: return@filter false
            if (atom.action == PermanentCostAction.TAP && container.has<TappedComponent>()) return@filter false
            predicateEvaluator.matches(state, projected, entityId, atom.filter, context)
        }
    }

    /**
     * True when [playerId] can pay [atom] at all — enough candidates to clear both the count floor
     * and (taking every candidate) the measure floor. Used to mark a cast variant unaffordable
     * rather than offering a cast the caster can't complete (CR 601.2h).
     */
    fun canPay(
        state: GameState,
        playerId: EntityId,
        atom: CostAtom.VariablePermanents,
        sourceId: EntityId? = null,
    ): Boolean {
        val candidates = candidates(state, playerId, atom, sourceId)
        if (candidates.size < atom.minCount) return false
        if (atom.minMeasure <= 0) return true
        return measure(state, atom.xMeasure, candidates) >= atom.minMeasure
    }
}
