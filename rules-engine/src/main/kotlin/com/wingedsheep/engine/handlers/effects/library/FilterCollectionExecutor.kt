package com.wingedsheep.engine.handlers.effects.library

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.DynamicAmountEvaluator
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.PredicateContext
import com.wingedsheep.engine.handlers.PredicateEvaluator
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.handlers.effects.TargetResolutionUtils
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.CollectionFilter
import com.wingedsheep.sdk.scripting.effects.FilterCollectionEffect
import kotlin.reflect.KClass

/**
 * Executor for FilterCollectionEffect.
 *
 * Splits a named collection into matching and non-matching subsets based on
 * a [CollectionFilter]. This is a purely automatic filter with no player choice.
 */
class FilterCollectionExecutor : EffectExecutor<FilterCollectionEffect> {

    override val effectType: KClass<FilterCollectionEffect> = FilterCollectionEffect::class

    private val predicateEvaluator = PredicateEvaluator()
    private val amountEvaluator = DynamicAmountEvaluator()

    override fun execute(
        state: GameState,
        effect: FilterCollectionEffect,
        context: EffectContext
    ): EffectResult {
        val cards = context.pipeline.storedCollections[effect.from]
            ?: return EffectResult.error(state, "No collection named '${effect.from}' in storedCollections")

        val filter = effect.filter
        val projected = state.projectedState
        val (matching, nonMatching) = when (filter) {
            is CollectionFilter.ExcludeSubtypesFromStored -> {
                val excludedSubtypes = context.pipeline.storedStringLists[filter.storedKey]
                    ?.map { Subtype(it) }?.toSet() ?: emptySet()

                cards.partition { cardId ->
                    val cardComponent = state.getEntity(cardId)?.get<CardComponent>()
                    val subtypes = cardComponent?.typeLine?.subtypes ?: emptyList()
                    // "Matching" = does NOT have any excluded subtype (passes through the filter)
                    subtypes.none { it in excludedSubtypes }
                }
            }

            is CollectionFilter.SharesSubtypeWithSacrificed -> {
                val sacrificed = context.sacrificedPermanents.firstOrNull()
                if (sacrificed == null) {
                    emptyList<EntityId>() to cards
                } else {
                    val sacrificedSubtypes = sacrificed.subtypes.takeIf { it.isNotEmpty() }
                        ?: state.getEntity(sacrificed.entityId)?.get<CardComponent>()
                            ?.typeLine?.subtypes?.map { it.value }?.toSet()
                        ?: emptySet()

                    cards.partition { cardId ->
                        val creatureSubtypes = projected.getSubtypes(cardId)
                        creatureSubtypes.intersect(sacrificedSubtypes).isNotEmpty()
                    }
                }
            }

            is CollectionFilter.MatchesFilter -> {
                val predicateContext = PredicateContext.fromEffectContext(context)
                cards.partition { cardId ->
                    predicateEvaluator.matches(state, projected, cardId, filter.filter, predicateContext)
                }
            }

            is CollectionFilter.GreatestPower -> {
                val maxPower = cards.maxOfOrNull { projected.getPower(it) ?: Int.MIN_VALUE }
                if (maxPower == null || maxPower == Int.MIN_VALUE) {
                    emptyList<EntityId>() to cards
                } else {
                    cards.partition { (projected.getPower(it) ?: Int.MIN_VALUE) == maxPower }
                }
            }

            is CollectionFilter.LeastToughness -> {
                val minToughness = cards.minOfOrNull { projected.getToughness(it) ?: Int.MAX_VALUE }
                if (minToughness == null || minToughness == Int.MAX_VALUE) {
                    emptyList<EntityId>() to cards
                } else {
                    cards.partition { (projected.getToughness(it) ?: Int.MAX_VALUE) == minToughness }
                }
            }

            is CollectionFilter.GreatestManaValue -> {
                // A face-down permanent has no mana cost (CR 708.2a), so its mana value is 0
                // (CR 202.3a): a gathered battlefield collection (Break Under Pressure) never
                // ranks a morph by its hidden cost.
                fun manaValueOf(cardId: EntityId): Int =
                    if (projected.getProjectedValues(cardId)?.isFaceDown == true) 0
                    else state.getEntity(cardId)?.get<CardComponent>()?.manaValue ?: Int.MIN_VALUE
                val maxManaValue = cards.maxOfOrNull { manaValueOf(it) }
                if (maxManaValue == null || maxManaValue == Int.MIN_VALUE) {
                    emptyList<EntityId>() to cards
                } else {
                    cards.partition { manaValueOf(it) == maxManaValue }
                }
            }

            is CollectionFilter.ManaValueAtMost -> {
                val maxManaValue = amountEvaluator.evaluate(state, filter.max, context)
                cards.partition { cardId ->
                    val cardComponent = state.getEntity(cardId)?.get<CardComponent>()
                    val manaValue = cardComponent?.manaValue ?: 0
                    manaValue <= maxManaValue
                }
            }

            is CollectionFilter.ManaValueEquals -> {
                val exactManaValue = amountEvaluator.evaluate(state, filter.value, context)
                cards.partition { cardId ->
                    val cardComponent = state.getEntity(cardId)?.get<CardComponent>()
                    val manaValue = cardComponent?.manaValue ?: 0
                    manaValue == exactManaValue
                }
            }

            is CollectionFilter.ExcludeEntity -> {
                val excludedId = TargetResolutionUtils.resolveEntityReference(filter.entity, context, state)
                cards.partition { it != excludedId }
            }

            is CollectionFilter.ExcludeOtherCollection -> {
                val excluded = context.pipeline.storedCollections[filter.otherCollectionName]
                    ?.toSet() ?: emptySet()
                cards.partition { it !in excluded }
            }

            is CollectionFilter.InZone -> {
                cards.partition { cardId ->
                    state.zones.entries.any { (key, entities) ->
                        key.zoneType == filter.zone && cardId in entities
                    }
                }
            }
        }

        val updatedCollections = mutableMapOf(effect.storeMatching to matching)
        val storeNonMatching = effect.storeNonMatching
        if (storeNonMatching != null) {
            updatedCollections[storeNonMatching] = nonMatching
        }

        return EffectResult.success(state).copy(updatedCollections = updatedCollections)
    }
}
