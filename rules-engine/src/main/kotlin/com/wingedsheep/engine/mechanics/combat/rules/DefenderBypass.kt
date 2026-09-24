package com.wingedsheep.engine.mechanics.combat.rules

import com.wingedsheep.engine.handlers.ConditionEvaluator
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.PredicateContext
import com.wingedsheep.engine.handlers.PredicateEvaluator
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.combat.CanAttackDespiteDefenderThisTurnComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.CanAttackDespiteDefender
import com.wingedsheep.sdk.scripting.filters.unified.Scope

/**
 * Single source of truth for "is the Defender restriction currently lifted for this creature?".
 *
 * A creature with defender can't attack (CR 702.3b) unless an effect lets it. Three things can
 * lift that restriction:
 *  - a temporary "attack this turn as though it didn't have defender" grant
 *    ([CanAttackDespiteDefenderThisTurnComponent], e.g. Krotiq Nestguard's activated ability),
 *  - a [CanAttackDespiteDefender] static ability scoped to the creature itself whose condition
 *    currently holds (e.g. Shipwreck Sentry / Mechan Shieldmate once an artifact entered this turn),
 *    or
 *  - a battlefield-scoped [CanAttackDespiteDefender] on any permanent whose group filter matches
 *    the creature (Ghalta the Immovable: "Creatures you control can attack as though they didn't
 *    have defender"). The filter is matched with that permanent as predicate source and its
 *    controller as "you", against the current projection. A face-down permanent has no abilities
 *    (CR 708.2) and contributes nothing.
 *
 * This does NOT check whether the creature actually has the Defender keyword — callers gate on that
 * (attack legality only cares when Defender is present; the display badge only shows on defenders).
 * Consulted by both [DefenderAttackRule] (enforcement) and `ClientStateTransformer` (the display
 * badge), so what the player SEES stays in sync with what the rules let them DO.
 */
object DefenderBypass {
    private val conditionEvaluator = ConditionEvaluator()
    private val predicateEvaluator = PredicateEvaluator()

    fun isActive(
        state: GameState,
        entityId: EntityId,
        controllerId: EntityId,
        cardRegistry: CardRegistry
    ): Boolean {
        val container = state.getEntity(entityId) ?: return false

        // Temporary this-turn grant.
        if (container.has<CanAttackDespiteDefenderThisTurnComponent>()) return true

        // Static "can attack despite defender as long as <condition>" printed on the creature.
        val cardComp = container.get<CardComponent>()
        val cardDef = cardComp?.let { cardRegistry.getCard(it.cardDefinitionId) }
        if (cardDef != null && !container.has<FaceDownComponent>()) {
            val effectContext = EffectContext(sourceId = entityId, controllerId = controllerId)
            val selfBypass = cardDef.staticAbilities
                .filterIsInstance<CanAttackDespiteDefender>()
                .filter { it.filter.scope is Scope.Self }
                .any { conditionHolds(state, it, effectContext) }
            if (selfBypass) return true
        }

        return battlefieldBypass(state, entityId, cardRegistry)
    }

    /** A battlefield-scoped grant on any permanent (possibly the creature itself) covers [entityId]. */
    private fun battlefieldBypass(
        state: GameState,
        entityId: EntityId,
        cardRegistry: CardRegistry
    ): Boolean {
        val projected = state.projectedState
        for (permanentId in state.getBattlefield()) {
            val permanent = state.getEntity(permanentId) ?: continue
            if (permanent.has<FaceDownComponent>()) continue
            val permCard = permanent.get<CardComponent>() ?: continue
            val abilities = cardRegistry.getCard(permCard.cardDefinitionId)?.staticAbilities ?: continue
            val grants = abilities.filterIsInstance<CanAttackDespiteDefender>()
                .filter { it.filter.scope is Scope.Battlefield }
            if (grants.isEmpty()) continue
            val permController = projected.getController(permanentId) ?: continue
            val predicateContext = PredicateContext(controllerId = permController, sourceId = permanentId)
            val effectContext = EffectContext(sourceId = permanentId, controllerId = permController)
            for (grant in grants) {
                if (grant.filter.excludeSelf && permanentId == entityId) continue
                if (!predicateEvaluator.matches(state, projected, entityId, grant.filter.baseFilter, predicateContext)) continue
                if (conditionHolds(state, grant, effectContext)) return true
            }
        }
        return false
    }

    private fun conditionHolds(
        state: GameState,
        ability: CanAttackDespiteDefender,
        context: EffectContext
    ): Boolean = ability.condition?.let { conditionEvaluator.evaluate(state, it, context) } ?: true
}
