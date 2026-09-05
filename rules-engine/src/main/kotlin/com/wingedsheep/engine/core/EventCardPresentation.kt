package com.wingedsheep.engine.core

import com.wingedsheep.sdk.model.EntityId
import kotlinx.serialization.Serializable

/**
 * The semantic and public identities of one card at event time, plus the viewers entitled to the
 * semantic identity.
 *
 * This is trusted engine data: [semanticName] may be private and [identityViewers] records private
 * audience facts. Only the value returned by [nameFor] is safe to expose to the requested
 * recipient. Client projection must never infer a past audience from a later state.
 */
@Serializable
data class EventCardPresentation(
    val semanticName: String,
    val publicName: String,
    val identityViewers: Set<EntityId> = emptySet(),
) {
    /** The name this recipient was entitled to see when the event was emitted. */
    fun nameFor(viewingPlayerId: EntityId, isSpectator: Boolean = false): String =
        if (!isSpectator && viewingPlayerId in identityViewers) semanticName else publicName
}
