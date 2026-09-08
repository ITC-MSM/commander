package com.wingedsheep.engine.handlers

import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.targets.TargetRequirement
import com.wingedsheep.sdk.scripting.values.EntityReference

/** Select mandatory single-object targets in order when a filter reads an earlier target. */
object DependentTargetSelection {
    fun isRequired(requirements: List<TargetRequirement>): Boolean = requirements.any {
        it is TargetObject && referencesTarget(it.filter.baseFilter)
    }

    private fun referencesTarget(filter: GameObjectFilter): Boolean =
        filter.cardPredicates.any(::referencesTarget) || filter.anyOf.any(::referencesTarget)

    private fun referencesTarget(predicate: CardPredicate): Boolean = when (predicate) {
        is CardPredicate.And -> predicate.predicates.any(::referencesTarget)
        is CardPredicate.Or -> predicate.predicates.any(::referencesTarget)
        is CardPredicate.Not -> referencesTarget(predicate.predicate)
        else -> (when (predicate) {
            is CardPredicate.PowerAtMostEntity -> predicate.reference
            is CardPredicate.PowerLessThanEntity -> predicate.reference
            is CardPredicate.PowerGreaterThanEntity -> predicate.reference
            is CardPredicate.ManaValueAtMostEntity -> predicate.reference
            is CardPredicate.SharesColorWith -> predicate.entity
            is CardPredicate.SharesCardTypeWith -> predicate.entity
            is CardPredicate.SharesCreatureTypeWith -> predicate.entity
            is CardPredicate.SharesManaValueWith -> predicate.entity
            is CardPredicate.SharesNameWith -> predicate.entity
            else -> null
        }) is EntityReference.Target
    }

    /**
     * Exclude choices that cannot complete the remaining requirements. Search stops at the first
     * completion; no Cartesian product is allocated. Ordinary independent targets never use this path.
     * No priority passes between these decisions, so the prefix remains on the same battlefield.
     */
    fun legalNext(
        state: GameState,
        requirements: List<TargetRequirement>,
        chosen: List<EntityId>,
        context: PredicateContext,
    ): List<EntityId> {
        require(requirements.all { it is TargetObject && it.filter.zone == Zone.BATTLEFIELD &&
            it.count == 1 && it.effectiveMinCount == 1 && !it.unlimited }) {
            "Dependent target selection requires mandatory single-permanent target slots"
        }
        val finder = TargetFinder()
        fun candidates(prefix: List<EntityId>): List<EntityId> = finder.findLegalTargets(
            state, requirements[prefix.size], context.controllerId,
            sourceId = context.sourceId,
            targetingSourceType = TargetingSourceType.ABILITY,
            triggeringEntityId = context.triggeringEntityId,
            pipelineContext = context.copy(targets = prefix.map { ChosenTarget.Permanent(it) }),
        )
        fun canComplete(prefix: List<EntityId>): Boolean =
            prefix.size == requirements.size || candidates(prefix).any { canComplete(prefix + it) }
        return candidates(chosen).filter { canComplete(chosen + it) }
    }
}
