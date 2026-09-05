package com.wingedsheep.engine.handlers.effects.linkedexile

import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.state.components.battlefield.BattlefieldEntryTimestampComponent
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.engine.state.components.battlefield.LinkedExileComponent
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId

/**
 * Linked-exile reads for live sources and resolving abilities. Live-source queries use the
 * component index; an ability from a departed battlefield visit reads that visit's retained pile.
 * Zone removal invalidates links, and membership checks also exclude vanished tokens.
 */
object LinkedExileLookup {

    /** Read the original source visit, even after that entity has returned or ceased to exist. */
    fun exiledCards(state: GameState, context: EffectContext): List<EntityId> {
        val sourceId = context.sourceId ?: return emptyList()
        val timestamp = context.sourceBattlefieldTimestamp
        val current = state.getEntity(sourceId)
            ?.get<BattlefieldEntryTimestampComponent>()?.timestamp
        if (timestamp != null && timestamp != current) {
            return state.departedLinkedExile[timestamp].orEmpty().filter { isStillExiled(state, it) }
        }
        return exiledCards(state, sourceId)
    }

    /** Append to the resolving source visit, which need no longer be on the battlefield. */
    fun link(state: GameState, context: EffectContext, cards: List<EntityId>): GameState {
        val sourceId = context.sourceId ?: return state
        val timestamp = context.sourceBattlefieldTimestamp
        val current = state.getEntity(sourceId)
            ?.get<BattlefieldEntryTimestampComponent>()?.timestamp
        if (timestamp != null && timestamp != current) {
            return state.copy(departedLinkedExile = state.departedLinkedExile +
                (timestamp to (state.departedLinkedExile[timestamp].orEmpty() + cards)))
        }
        val existing = state.getEntity(sourceId)?.get<LinkedExileComponent>()?.exiledIds.orEmpty()
        return state.updateEntity(sourceId) { it.with(LinkedExileComponent(existing + cards)) }
    }

    /**
     * The ids in [sourceId]'s linked-exile pile that are still in an exile zone, in exile order
     * (oldest first). Empty when the source has no pile, or when every card in it has left exile.
     */
    fun exiledCards(state: GameState, sourceId: EntityId?): List<EntityId> {
        val linked = sourceId?.let { state.getEntity(it)?.get<LinkedExileComponent>() } ?: return emptyList()
        return linked.exiledIds.filter { isStillExiled(state, it) }
    }

    /**
     * The [index]-th card still exiled with [sourceId], or null when the pile is shorter than that.
     * Backs [com.wingedsheep.sdk.scripting.values.EntityReference.LinkedExiledCard], whose default
     * index 0 is "the exiled card" of every Imprint permanent (Imprint exiles exactly one).
     */
    fun exiledCard(state: GameState, sourceId: EntityId?, index: Int = 0): EntityId? =
        exiledCards(state, sourceId).getOrNull(index)

    /**
     * Whether [entityId] is currently in its owner's exile zone. Uses the owner-keyed zone directly
     * where the owner is known, falling back to a scan for the ownerless case (a token that ceased
     * to exist has neither, and correctly reports false).
     */
    private fun isStillExiled(state: GameState, entityId: EntityId): Boolean {
        val container = state.getEntity(entityId) ?: return false
        val ownerId = container.get<OwnerComponent>()?.playerId
            ?: container.get<CardComponent>()?.ownerId
        if (ownerId != null) return entityId in state.getZone(ZoneKey(ownerId, Zone.EXILE))
        return state.zones.any { (zone, cards) -> zone.zoneType == Zone.EXILE && entityId in cards }
    }
}
