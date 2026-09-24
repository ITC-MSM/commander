package com.wingedsheep.engine.view

import com.wingedsheep.engine.handlers.ConditionEvaluator
import com.wingedsheep.engine.handlers.PredicateEvaluator
import com.wingedsheep.engine.legalactions.utils.CastPermissionUtils
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.PlayerYields
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.player.HotseatControlComponent
import com.wingedsheep.engine.view.projection.CardActiveEffectsProjector
import com.wingedsheep.engine.view.projection.CardFacesProjector
import com.wingedsheep.engine.view.projection.CardProjector
import com.wingedsheep.engine.view.projection.CombatProjector
import com.wingedsheep.engine.view.projection.ConditionBadgeProjector
import com.wingedsheep.engine.view.projection.DeckListProjector
import com.wingedsheep.engine.view.projection.PlayerActiveEffectsProjector
import com.wingedsheep.engine.view.projection.PlayerProjector
import com.wingedsheep.engine.view.projection.SpellOnStackProjector
import com.wingedsheep.engine.view.projection.StackItemProjector
import com.wingedsheep.engine.view.projection.StackTextRenderer
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId

/**
 * Transforms internal game state into client-facing DTOs.
 *
 * This class:
 * - Masks hidden information (opponent's hand, libraries)
 * - Transforms internal components into explicit DTO fields
 * - Applies continuous effects to show "true" card state
 * - Prevents information leakage by only including relevant data
 *
 * It is the orchestrator: it walks the zones, decides which cards the viewer gets details for, and
 * assembles the [ClientGameState]. Each concern is projected in `engine.view.projection` — cards
 * ([CardProjector]), abilities on the stack ([StackItemProjector]), players ([PlayerProjector]),
 * combat ([CombatProjector]) and the viewer's deck tracker ([DeckListProjector]).
 */
class ClientStateTransformer(
    private val cardRegistry: CardRegistry,
    private val debugMode: Boolean = false
) {

    private val conditionEvaluator = ConditionEvaluator()
    private val visibility = Visibility(cardRegistry, debugMode)
    private val stackText = StackTextRenderer(conditionEvaluator, visibility)
    private val conditionBadges = ConditionBadgeProjector(cardRegistry, conditionEvaluator)
    private val cardProjector = CardProjector(
        cardRegistry = cardRegistry,
        visibility = visibility,
        conditionEvaluator = conditionEvaluator,
        activeEffects = CardActiveEffectsProjector(cardRegistry, visibility, conditionBadges),
        conditionBadges = conditionBadges,
        spellOnStackProjector = SpellOnStackProjector(stackText),
        // Finds loyalty abilities granted by statics so the planeswalker menu can list them.
        facesProjector = CardFacesProjector(
            cardRegistry,
            CastPermissionUtils(cardRegistry, PredicateEvaluator(), conditionEvaluator),
            stackText
        ),
    )
    private val stackItemProjector = StackItemProjector(cardRegistry, visibility, stackText)
    private val playerProjector = PlayerProjector(cardRegistry, conditionEvaluator, PlayerActiveEffectsProjector())
    private val combatProjector = CombatProjector()
    private val deckListProjector = DeckListProjector(cardRegistry, visibility)

    /**
     * Transform the game state for a specific player's view.
     *
     * @param state The full game state
     * @param viewingPlayerId The player who will see this state
     * @return Client-safe game state DTO
     */
    fun transform(
        state: GameState,
        viewingPlayerId: EntityId,
        isSpectator: Boolean = false
    ): ClientGameState {
        // Project the game state to apply continuous effects (Rule 613)
        val projectedState = state.projectedState

        // Build visible cards map
        val cards = mutableMapOf<EntityId, ClientCard>()

        // Process all zones
        val zones = mutableListOf<ClientZone>()

        for ((zoneKey, entityIds) in state.zones) {
            val isZoneVisible = visibility.isZoneVisibleTo(state, zoneKey, viewingPlayerId, isSpectator)

            // For libraries we always send the full ordered list of entity IDs so the client can
            // render a correctly sized stack. Individual card *details* are only populated for cards
            // that have been revealed to the viewing player (Scry, Surveil, look-at-top-N, etc.).
            // Unrevealed slots end up as opaque IDs the client renders as card backs.
            val isLibrary = zoneKey.zoneType == Zone.LIBRARY
            val cardsWithDetails = if (isZoneVisible) {
                entityIds
            } else {
                entityIds.filter { entityId ->
                    visibility.isCardIdentityVisibleTo(
                        state,
                        zoneKey,
                        entityId,
                        viewingPlayerId,
                        isSpectator,
                    )
                }
            }
            val zoneCardIds = if (isLibrary) entityIds else cardsWithDetails

            zones.add(
                ClientZone(
                    zoneId = zoneKey,
                    cardIds = zoneCardIds,
                    size = entityIds.size,
                    isVisible = isZoneVisible || cardsWithDetails.isNotEmpty() || isLibrary
                )
            )

            // Include card details for visible cards (either whole zone visible, or individually revealed)
            for (entityId in cardsWithDetails) {
                val clientCard = cardProjector.project(state, entityId, zoneKey, projectedState, viewingPlayerId, isSpectator)
                if (clientCard != null) {
                    cards[entityId] = clientCard
                }
            }
        }

        // --- FIX START: Ensure Battlefield is always present ---
        if (zones.none { it.zoneId.zoneType == Zone.BATTLEFIELD }) {
            val bfZoneKey = ZoneKey(viewingPlayerId, Zone.BATTLEFIELD)
            val bfEntities = state.getBattlefield()

            zones.add(
                ClientZone(
                    zoneId = bfZoneKey,
                    cardIds = bfEntities,
                    size = bfEntities.size,
                    isVisible = true
                )
            )

            // Add cards if they happen to exist (rare if zone was missing from map, but good for safety)
            for (entityId in bfEntities) {
                if (entityId !in cards) {
                    val clientCard = cardProjector.project(state, entityId, bfZoneKey, projectedState, viewingPlayerId, isSpectator)
                    if (clientCard != null) {
                        cards[entityId] = clientCard
                    }
                }
            }
        }
        // --- FIX END ---

        // --- FIX START: Ensure Stack is always present ---
        if (zones.none { it.zoneId.zoneType == Zone.STACK }) {
            val stackZoneKey = ZoneKey(viewingPlayerId, Zone.STACK)
            zones.add(
                ClientZone(
                    zoneId = stackZoneKey,
                    cardIds = state.stack,
                    size = state.stack.size,
                    isVisible = true
                )
            )

            // Include card details for stack items
            for (entityId in state.stack) {
                if (entityId !in cards) {
                    val clientCard = cardProjector.project(state, entityId, stackZoneKey, projectedState, viewingPlayerId, isSpectator)
                        ?: stackItemProjector.project(state, entityId, viewingPlayerId, isSpectator)
                    if (clientCard != null) {
                        cards[entityId] = clientCard
                    }
                }
            }
        }
        // --- FIX END ---

        // Build player information
        val players = state.turnOrder.map { playerId ->
            playerProjector.project(state, playerId)
        }

        // Build combat state if in combat
        val combat = combatProjector.project(state)

        // Get active and priority players, defaulting to first player if not set
        val activePlayerId = state.activePlayerId ?: state.turnOrder.firstOrNull() ?: viewingPlayerId
        val priorityPlayerId = state.priorityPlayerId ?: activePlayerId

        // Hotseat (play-against-yourself): the viewing player holds input authority for
        // every seat via HotseatControlComponent. Spectators never get hotseat control.
        val hotseat = !isSpectator && state.turnOrder.any { playerId ->
            state.getEntity(playerId)
                ?.get<HotseatControlComponent>()
                ?.controllerId == viewingPlayerId
        }

        // Hijack indicators (Mindslaver-style). For every other player whose turn the
        // viewing player currently controls, set youAreHijacking = that player. If the
        // viewing player is themselves being controlled, set youAreHijackedBy. Skipped in
        // hotseat, where actorFor also redirects but the dedicated [hotseat] flag drives UI.
        var youAreHijacking: EntityId? = null
        var youAreHijackedBy: EntityId? = null
        if (!hotseat) {
            for (playerId in state.turnOrder) {
                val actor = state.actorFor(playerId)
                if (playerId == viewingPlayerId && actor != viewingPlayerId) {
                    youAreHijackedBy = actor
                } else if (playerId != viewingPlayerId && actor == viewingPlayerId) {
                    youAreHijacking = playerId
                }
            }
        }

        // Persistent yields are private to each player: only ever surface the viewer's own.
        val activeYields = if (isSpectator) emptyList() else buildClientYields(state.yieldsFor(viewingPlayerId))

        // The deck tracker is the viewer's own decklist — never a spectator's or an opponent's.
        val deck = if (isSpectator) emptyList() else deckListProjector.project(state, viewingPlayerId)

        return ClientGameState(
            viewingPlayerId = viewingPlayerId,
            cards = cards,
            zones = zones,
            players = players,
            currentPhase = state.phase,
            currentStep = state.step,
            activePlayerId = activePlayerId,
            priorityPlayerId = priorityPlayerId,
            turnNumber = state.turnNumber,
            isGameOver = state.gameOver,
            winnerId = state.winnerId,
            combat = combat,
            voidActive = state.nonlandPermanentLeftBattlefieldThisTurn || state.spellWarpedThisTurn,
            dayNight = state.dayNight,
            youAreHijacking = youAreHijacking,
            youAreHijackedBy = youAreHijackedBy,
            hotseat = hotseat,
            activeYields = activeYields,
            deck = deck
        )
    }

    /**
     * Flatten a player's [PlayerYields] into one [ClientYield] per
     * ability identity, merging the auto-pass scopes and the auto-answer into a single display row.
     * The display name is the card name carried in the definition id (`"Name#SET-123"` → `"Name"`).
     */
    private fun buildClientYields(yields: PlayerYields): List<ClientYield> {
        val identities = yields.untilEndOfTurn + yields.wholeGame + yields.autoAnswer.keys
        return identities.map { id ->
            ClientYield(
                cardDefinitionId = id.cardDefinitionId,
                abilityId = id.abilityId.value,
                displayName = id.cardDefinitionId.substringBefore("#"),
                untilEndOfTurn = id in yields.untilEndOfTurn,
                wholeGame = id in yields.wholeGame,
                autoAnswer = yields.autoAnswer[id]
            )
        }
    }
}
