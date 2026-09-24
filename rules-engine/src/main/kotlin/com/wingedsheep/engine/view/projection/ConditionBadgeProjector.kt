package com.wingedsheep.engine.view.projection

import com.wingedsheep.engine.handlers.ConditionEvaluator
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.view.ClientCardEffect
import com.wingedsheep.engine.view.ClientDeliriumInfo
import com.wingedsheep.engine.view.ClientThresholdInfo
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.conditions.AllConditions
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.Condition

/**
 * Progress toward the conditions a card is waiting on: threshold and delirium counters, and one
 * badge per countable clause of a triggered ability's intervening-if ("Creatures in GY: 2/4",
 * a Case's "2/3 sources dealt damage").
 */
internal class ConditionBadgeProjector(
    private val cardRegistry: CardRegistry,
    private val conditionEvaluator: ConditionEvaluator,
) {

    /**
     * Threshold-style progress badge: shown on cards with a static ability gated on "controller's
     * graveyard has at least N cards" ([CardDefinition.graveyardThreshold]).
     */
    fun thresholdInfo(state: GameState, cardDef: CardDefinition?, controllerId: EntityId): ClientThresholdInfo? =
        cardDef?.graveyardThreshold?.let { required ->
            val current = state.getGraveyard(controllerId).size
            ClientThresholdInfo(
                current = current,
                required = required,
                active = current >= required
            )
        }

    /**
     * Delirium progress badge: shown only on cards whose definition actually gates on delirium
     * ("four or more card types among cards in your graveyard") wherever it lives — static,
     * triggered or activated ability, spell effect, cost reduction, replacement
     * ([CardDefinition.deliriumThreshold]). Unlike threshold, this counts distinct card types, not
     * raw graveyard size.
     */
    fun deliriumInfo(state: GameState, cardDef: CardDefinition?, controllerId: EntityId): ClientDeliriumInfo? =
        cardDef?.deliriumThreshold?.let { required ->
            val current = distinctGraveyardCardTypes(state, controllerId)
            ClientDeliriumInfo(
                current = current,
                required = required,
                active = current >= required
            )
        }

    /**
     * Distinct card types among cards in [playerId]'s graveyard — the Delirium count (CR; active
     * at 4+). Mirrors the engine's `Aggregation.DISTINCT_TYPES`. Graveyard is a non-battlefield
     * zone, so base card types are correct here (no projection needed).
     */
    private fun distinctGraveyardCardTypes(state: GameState, playerId: EntityId): Int =
        state.getGraveyard(playerId)
            .flatMapTo(mutableSetOf()) { entityId ->
                state.getEntity(entityId)?.get<CardComponent>()?.typeLine?.cardTypes?.map { it.name } ?: emptyList()
            }
            .size

    /**
     * Build badges showing progress toward intervening-if trigger conditions.
     * For example, Oversold Cemetery shows "Creatures in GY: 2/4", and an unsolved Case shows how
     * far along its "to solve" criterion is ("2/3 sources dealt damage").
     */
    fun triggerConditionBadges(
        state: GameState,
        entityId: EntityId
    ): List<ClientCardEffect> {
        val container = state.getEntity(entityId) ?: return emptyList()
        val cardComponent = container.get<CardComponent>() ?: return emptyList()
        val cardDef = cardRegistry.getCard(cardComponent.cardDefinitionId) ?: return emptyList()
        val controllerId = container.get<ControllerComponent>()?.playerId ?: return emptyList()

        val badges = mutableListOf<ClientCardEffect>()

        for (ability in cardDef.triggeredAbilities) {
            val condition = ability.triggerCondition ?: continue
            badges.addAll(evaluateConditionBadges(state, condition, controllerId, entityId))
        }

        // The client keys badges by effectId, so a card with two countable conditions — two
        // intervening-ifs, or one composite carrying two of them — needs them distinguished. The
        // first keeps the bare id so existing badges are untouched.
        return badges.mapIndexed { index, badge ->
            if (index == 0) badge else badge.copy(effectId = "${badge.effectId}_$index")
        }
    }

    /**
     * The progress badges one triggered ability's condition contributes: one per countable clause,
     * none while an uncountable clause is already false.
     */
    private fun evaluateConditionBadges(
        state: GameState,
        condition: Condition,
        controllerId: EntityId,
        sourceId: EntityId,
    ): List<ClientCardEffect> {
        // The badge must evaluate against the permanent that owns the ability: conditions
        // routinely read `EntityReference.Source` (counters on this permanent, its power,
        // whether it's attacking). With a null sourceId those resolve to 0, so the badge
        // reads a permanently-stuck "0/N" while the real condition works fine — e.g. MSH's
        // Plan enchantments, whose "the number of plan counters" badge never moved.
        val context = EffectContext(
            sourceId = sourceId,
            controllerId = controllerId,
        )
        // An intervening-if with more than one clause is one condition to the engine and several
        // to a player. Every Case's "to solve" is such a composite — the printed criterion ANDed
        // with "and this Case is not solved" (CR 719.3a) — so without splitting it, the very cards
        // whose whole point is a countdown were the ones showing no countdown at all.
        val parts = when (condition) {
            is AllConditions -> condition.conditions
            else -> listOf(condition)
        }
        val progress = parts.map { part -> part to conditionEvaluator.countProgress(state, part, context) }
        // A clause that can't be counted is a gate, and a gate that's already shut makes the count
        // moot: a solved Case would otherwise sit on a stale "5/3" for the rest of the game,
        // because "not solved" — not the criterion — is what stops it triggering again.
        val gateShut = progress.any { (part, counted) ->
            counted == null && !conditionEvaluator.evaluate(state, part, context)
        }
        if (gateShut) return emptyList()

        return progress.mapNotNull { (part, counted) ->
            counted?.let {
                ClientCardEffect(
                    effectId = "condition_compare",
                    name = "${it.current}/${it.required}",
                    description = "${describeCountedClause(part)} (${it.current}/${it.required})",
                    icon = if (it.met) "condition-met" else "condition-unmet"
                )
            }
        }
    }

    /**
     * How a counted clause is named in its badge tooltip: the *quantity* it counts, not the whole
     * comparison. A [Compare] names its left operand ("the number of creature cards in your
     * graveyard"), which reads as a label beside the "2/4" rather than repeating the threshold that
     * is already there. The clause shapes that have no left operand — "you control no suspected
     * Skeletons", "you've cast N spells this turn" — fall back to describing themselves.
     */
    private fun describeCountedClause(condition: Condition): String = when (condition) {
        is Compare -> condition.left.description
        else -> condition.description
    }
}
