package com.wingedsheep.sdk.model

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.AllConditions
import com.wingedsheep.sdk.scripting.conditions.AnyCondition
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.conditions.Condition
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.Aggregation
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.serialization.ScriptTreeWalker

/**
 * Derives the graveyard gates a card turns on at — the numbers behind
 * [CardDefinition.graveyardThreshold] and [CardDefinition.deliriumThreshold]. Read-only analysis of
 * the typed script; nothing here is authored or serialized.
 */
internal object GraveyardThresholds {

    /**
     * The smallest N for which one of [definition]'s static abilities is gated on "you have at least
     * N cards in your graveyard" (threshold), or null.
     *
     * Deliberately narrow: only the gate conditions of [ConditionalStaticAbility]s, and inside them
     * only `and` / `or` composition. A negated clause (`NotCondition`) turns off as the graveyard
     * fills rather than on, and any other condition shape is not a graveyard-size gate — so every
     * other [Condition] contributes nothing, which is what the catch-all branch below says.
     */
    fun graveyardSize(definition: CardDefinition): Int? =
        definition.script.staticAbilities
            .filterIsInstance<ConditionalStaticAbility>()
            .flatMap { graveyardSizeGates(it.condition) }
            .minOrNull()

    private fun graveyardSizeGates(condition: Condition): List<Int> = when (condition) {
        is Compare -> listOfNotNull(atLeast(condition, ::isYourGraveyardSize))
        is AllConditions -> condition.conditions.flatMap(::graveyardSizeGates)
        is AnyCondition -> condition.conditions.flatMap(::graveyardSizeGates)
        else -> emptyList()
    }

    /**
     * The smallest N for which [definition] gates anything on "N or more card types among cards in
     * your graveyard" (delirium), or null.
     *
     * Delirium can sit anywhere — a static, triggered or activated ability, a spell effect, a cost
     * reduction, a replacement effect, the back face — so this looks at every node of the card
     * through [ScriptTreeWalker] rather than enumerating containers by hand.
     */
    fun delirium(definition: CardDefinition): Int? {
        val found = mutableListOf<Int>()
        ScriptTreeWalker.forEachNode(definition) { node ->
            if (node is Compare) atLeast(node, ::isYourDistinctGraveyardTypes)?.let(found::add)
        }
        return found.minOrNull()
    }

    /**
     * Matches `counted >= Fixed(N)` in any of its four spellings (`>=`, `>`, and their mirrored
     * `<=`, `<` forms with the count on the right) and returns the N at which it turns on.
     */
    private fun atLeast(compare: Compare, counts: (DynamicAmount) -> Boolean): Int? {
        val left = compare.left
        val right = compare.right
        return when (compare.operator) {
            ComparisonOperator.GTE -> if (counts(left)) (right as? DynamicAmount.Fixed)?.amount else null
            ComparisonOperator.LTE -> if (counts(right)) (left as? DynamicAmount.Fixed)?.amount else null
            ComparisonOperator.GT -> if (counts(left)) (right as? DynamicAmount.Fixed)?.amount?.plus(1) else null
            ComparisonOperator.LT -> if (counts(right)) (left as? DynamicAmount.Fixed)?.amount?.plus(1) else null
            ComparisonOperator.EQ, ComparisonOperator.NEQ -> null
        }
    }

    /**
     * `Count(You, GRAVEYARD, Any)`. A filtered count (only creature cards) is not threshold: the
     * badge tracks raw graveyard size.
     */
    private fun isYourGraveyardSize(amount: DynamicAmount): Boolean =
        amount is DynamicAmount.Count &&
            amount.player == Player.You &&
            amount.zone == Zone.GRAVEYARD &&
            amount.filter == GameObjectFilter.Any

    /** `AggregateZone(You, GRAVEYARD, *, DISTINCT_TYPES)` — the delirium count, whatever the filter. */
    private fun isYourDistinctGraveyardTypes(amount: DynamicAmount): Boolean =
        amount is DynamicAmount.AggregateZone &&
            amount.player == Player.You &&
            amount.zone == Zone.GRAVEYARD &&
            amount.aggregation == Aggregation.DISTINCT_TYPES
}
