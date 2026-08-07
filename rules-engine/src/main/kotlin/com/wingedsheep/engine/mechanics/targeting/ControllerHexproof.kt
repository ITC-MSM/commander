package com.wingedsheep.engine.mechanics.targeting

import com.wingedsheep.engine.handlers.ConditionEvaluator
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.battlefield.GrantsControllerHexproofComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.player.PlayerHexproofComponent
import com.wingedsheep.sdk.model.EntityId

/**
 * Player-level hexproof granted by a permanent ("You … have hexproof" — Shalai, Voice of Plenty;
 * Crystal Barricade; Captain America, Super-Soldier).
 *
 * Single home for the question so the four readers that ask it — [TargetValidator],
 * `TargetEnumerationUtils`, `TargetFinder` and `ClientStateTransformer` — can't drift. They had
 * four copies of the same battlefield scan, and a gated grant would otherwise have had to be
 * special-cased in each.
 *
 * The gate is the reason this exists rather than a bare component check.
 * [GrantsControllerHexproofComponent] is stamped once by `StaticAbilityHandler` as the permanent
 * enters, so a grant wrapped in a `ConditionalStaticAbility` ("**as long as** Captain America has a
 * shield counter on him, you … have hexproof") cannot be resolved at stamp time — the condition
 * flips later in the game, when the counter is spent. The condition therefore travels on the
 * component and is evaluated here, against current state, on every read.
 */
object ControllerHexproof {

    private val conditionEvaluator = ConditionEvaluator()

    /**
     * Whether [entityId] is *currently* granting hexproof to its controller — it carries the
     * marker, and either the grant is unconditional or its "as long as" gate holds right now.
     */
    fun isGrantingNow(state: GameState, entityId: EntityId): Boolean {
        val container = state.getEntity(entityId) ?: return false
        val marker = container.get<GrantsControllerHexproofComponent>() ?: return false
        val condition = marker.condition ?: return true
        val controllerId = state.projectedState.getController(entityId)
            ?: container.get<ControllerComponent>()?.playerId
            ?: return false
        return conditionEvaluator.evaluate(
            state,
            condition,
            EffectContext(sourceId = entityId, controllerId = controllerId),
        )
    }

    /**
     * Whether [playerId] has hexproof — either directly ([PlayerHexproofComponent], from a
     * resolution-time effect) or from a permanent they control that grants it.
     *
     * Note this is hexproof, not shroud: the player can still be targeted by their own spells and
     * abilities. Callers that need that distinction pair this with the caster
     * (see [appliesAgainst]).
     */
    fun appliesTo(state: GameState, playerId: EntityId): Boolean {
        if (state.getEntity(playerId)?.has<PlayerHexproofComponent>() == true) return true

        return state.getBattlefield().any { entityId ->
            val container = state.getEntity(entityId) ?: return@any false
            container.get<ControllerComponent>()?.playerId == playerId &&
                isGrantingNow(state, entityId)
        }
    }

    /**
     * Whether [playerId] has hexproof *against* [casterId] — hexproof never stops the player's own
     * spells and abilities (CR 702.11b), so this is false when they are the same player.
     */
    fun appliesAgainst(state: GameState, playerId: EntityId, casterId: EntityId): Boolean =
        playerId != casterId && appliesTo(state, playerId)
}
