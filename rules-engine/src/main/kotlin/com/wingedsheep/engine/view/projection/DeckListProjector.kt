package com.wingedsheep.engine.view.projection

import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.CopyOfComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.view.ClientDeckCard
import com.wingedsheep.engine.view.Visibility
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId

/** Projects the viewer's own deck tracker: what they started with and what is still unseen. */
internal class DeckListProjector(
    private val cardRegistry: CardRegistry,
    private val visibility: Visibility,
) {

    /**
     * Build [viewingPlayerId]'s own decklist: every non-token card they *own*, wherever it now is,
     * grouped by printed card.
     *
     * Deriving the list from live ownership rather than a stored decklist keeps it honest for free —
     * a stolen creature is still in its owner's deck (ownership never changes, CR 108.3), a token
     * copy of one never is, and a permanent copying something else counts as the card it was printed
     * as (that's what [CopyOfComponent] remembers).
     *
     * Zones: the sideboard is excluded as outside the game (CR 400.11a); the command zone is included
     * so a commander stays one stable row instead of appearing and vanishing as it's cast and
     * returns. The stack lives outside [GameState.zones], so it is walked separately.
     */
    fun project(state: GameState, viewingPlayerId: EntityId): List<ClientDeckCard> {
        class Tally {
            var copies = 0
            var remaining = 0
            /** Art from a real (non-copy) printing of this card in the game, if we saw one. */
            var printingImageUri: String? = null
        }

        val tallies = HashMap<String, Tally>()

        fun record(entityId: EntityId, zoneType: Zone) {
            val container = state.getEntity(entityId) ?: return
            if (container.has<TokenComponent>()) return
            val cardComponent = container.get<CardComponent>() ?: return
            val ownerId = cardComponent.ownerId ?: container.get<OwnerComponent>()?.playerId
            if (ownerId != viewingPlayerId) return

            // A copy effect overwrites CardComponent with the copied card; CopyOfComponent holds
            // what this card is actually printed as, which is what belongs in the decklist.
            val copyOf = container.get<CopyOfComponent>()
            val definitionId = copyOf?.originalCardDefinitionId ?: cardComponent.cardDefinitionId

            val tally = tallies.getOrPut(definitionId) { Tally() }
            tally.copies++
            // The zone walk below has a real key to hand and the stack has none, so let the
            // visibility authority derive it either way rather than assuming every zone is
            // owner-keyed — the battlefield is not.
            if (!visibility.isCardIdentityVisibleTo(state, zoneType, entityId, viewingPlayerId)) {
                tally.remaining++
            }
            if (copyOf == null && tally.printingImageUri == null) {
                tally.printingImageUri = cardComponent.imageUri
            }
        }

        for ((zoneKey, entityIds) in state.zones) {
            if (zoneKey.zoneType == Zone.SIDEBOARD) continue
            for (entityId in entityIds) record(entityId, zoneKey.zoneType)
        }
        for (entityId in state.stack) record(entityId, Zone.STACK)

        return tallies.mapNotNull { (definitionId, tally) ->
            val definition = cardRegistry.getCard(definitionId) ?: return@mapNotNull null
            ClientDeckCard(
                cardName = definition.name,
                copies = tally.copies,
                remaining = tally.remaining,
                cmc = definition.cmc,
                cardTypes = definition.typeLine.cardTypes.map { it.name },
                colors = definition.colors.map { it.name },
                imageUri = tally.printingImageUri ?: definition.metadata.imageUri
            )
        }.sortedWith(compareBy({ it.cmc }, { it.cardName }))
    }
}
