package com.wingedsheep.engine.view

import com.wingedsheep.engine.core.EventCardPresentation
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.FACE_DOWN_DISPLAY_NAME
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId

/**
 * Captures cast identities while both sides of the authoritative state transition still exist.
 *
 * [Visibility] remains the sole authority for every viewer-specific answer. The resulting
 * [EventCardPresentation] is immutable trusted event data: later projection selects one viewer's
 * answer from it rather than consulting a potentially changed game state.
 */
class EventPresentationFactory(
    private val visibility: Visibility,
) {
    fun castSpellIdentity(
        beforeCast: GameState,
        onStack: GameState,
        castFromZone: Zone?,
        entityId: EntityId,
        semanticName: String,
    ): EventCardPresentation {
        if (isPublicThroughCast(beforeCast, onStack, castFromZone, entityId)) {
            return EventCardPresentation(
                semanticName = semanticName,
                publicName = semanticName,
            )
        }

        val identityViewers = onStack.turnOrder
            .filter { viewerId ->
                visibility.isCardIdentityVisibleThroughCast(
                    beforeCast = beforeCast,
                    onStack = onStack,
                    castFromZone = castFromZone,
                    entityId = entityId,
                    viewingPlayerId = viewerId,
                )
            }
            .toSet()
        return EventCardPresentation(
            semanticName = semanticName,
            publicName = FACE_DOWN_DISPLAY_NAME,
            identityViewers = identityViewers,
        )
    }

    /** A spectator is entitled only to information [Visibility] calls public, never a seat's view. */
    private fun isPublicThroughCast(
        beforeCast: GameState,
        onStack: GameState,
        castFromZone: Zone?,
        entityId: EntityId,
    ): Boolean {
        val representativeViewer = onStack.turnOrder.firstOrNull() ?: return false
        return visibility.isCardIdentityVisibleThroughCast(
            beforeCast = beforeCast,
            onStack = onStack,
            castFromZone = castFromZone,
            entityId = entityId,
            viewingPlayerId = representativeViewer,
            isSpectator = true,
        )
    }
}
