package com.wingedsheep.engine.state

import com.wingedsheep.engine.state.components.battlefield.PhasedOutComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.engine.state.components.stack.SpellOnStackComponent
import com.wingedsheep.sdk.model.EntityId

/**
 * What a face-down permanent or a face-down spell is called instead of the card underneath it.
 *
 * CR 708.2a — a face-down permanent is a 2/2 creature with "no text, no name, no subtypes, and no
 * mana cost". CR 708.4 gives a face-down *spell* the same characteristics while it is on the stack.
 */
const val FACE_DOWN_DISPLAY_NAME: String = "Face-down creature"

/**
 * What a card lying face down in exile is called. Deliberately not [FACE_DOWN_DISPLAY_NAME]: an
 * exiled face-down card (a foretold card, an "exile face down" rider) is not a creature and has no
 * characteristics to show, which is why [com.wingedsheep.engine.view.ClientStateTransformer] draws
 * it with a different, smaller card view.
 */
const val FACE_DOWN_CARD_DISPLAY_NAME: String = "Face-down card"

/**
 * The name [entityId] may be shown under to *any* viewer, its controller included.
 *
 * Use this for flat strings with no audience attached — a game-log line, a decision summary. Those
 * reach both players, and [com.wingedsheep.engine.view.ClientEventTransformer] cannot redact them
 * downstream: it maps engine events to text with no `GameState` in hand, so it cannot tell a
 * face-down object from a face-up one. The decision has to be made where the event is emitted.
 *
 * Where the text *does* have an audience, prefer [nameVisibleTo] — a player may look at a
 * face-down object they control (CR 708.5).
 */
fun nameVisibleToAll(state: GameState, entityId: EntityId, fallback: String): String =
    faceDownDisplayName(state, entityId) ?: fallback

/**
 * The name [entityId] may be shown under to [viewingPlayerId] specifically.
 *
 * Same masking as [nameVisibleToAll], except that a player may look at a face-down object they
 * control (CR 708.5) and so keeps [fallback] for their own. A spectator never may — matching the
 * gate [com.wingedsheep.engine.view.ClientStateTransformer] applies to card views, so a per-viewer
 * label and the card it labels always agree.
 */
fun nameVisibleTo(
    state: GameState,
    entityId: EntityId,
    fallback: String,
    viewingPlayerId: EntityId,
    isSpectator: Boolean = false,
): String {
    val masked = faceDownDisplayName(state, entityId) ?: return fallback
    return if (identityVisibleTo(state, entityId, viewingPlayerId, isSpectator)) fallback else masked
}

/**
 * Whether [viewingPlayerId] may read [entityId]'s *printed* identity — the name, rules text, mana
 * cost and card definition of the card underneath.
 *
 * True for everything except a face-down object another player controls (CR 708.5). Use it to gate
 * the printed fields that no other layer masks: a face-down permanent's projected characteristics
 * are already the 2/2 with no name that CR 708.2a describes, but the card's own printed text has no
 * projection entry to hide behind.
 *
 * Controller-only, matching the rule. A grant that widens it (Lens of Clarity's "you may look at
 * face-down creatures") is layered on top by the caller that supports it — see
 * [com.wingedsheep.engine.view.ClientStateTransformer].
 */
fun identityVisibleTo(
    state: GameState,
    entityId: EntityId,
    viewingPlayerId: EntityId,
    isSpectator: Boolean = false,
): Boolean {
    faceDownDisplayName(state, entityId) ?: return true
    return !isSpectator && viewingPlayerId == playerWhoMayLookAt(state, entityId)
}

/**
 * The masked name for [entityId] if it is a face-down object in a zone where its identity is
 * hidden, or null if it has a name everyone may read.
 *
 * The three zones answer differently, which is the whole reason this lives in one place:
 *
 * - **Stack** — a spell cast face down carries no [FaceDownComponent]; that is stamped as it
 *   resolves. Its face-down status rides [SpellOnStackComponent.castFaceDown] instead. This is the
 *   same pair `ClientStateTransformer` tests when deciding whether to mask a card view.
 * - **Battlefield** — [FaceDownComponent], but checked against the *physical* zone. [GameState
 *   .getBattlefield] deliberately hides phased-out permanents, and a phased-out face-down permanent
 *   is still a face-down permanent whose identity must not leak.
 * - **Exile** — [FaceDownComponent] again, but the object is not a creature, so it gets
 *   [FACE_DOWN_CARD_DISPLAY_NAME].
 *
 * Anything else keeps its name, including a face-down object that has already left all three: its
 * owner reveals it to every player as it goes (CR 708.9).
 */
private fun faceDownDisplayName(state: GameState, entityId: EntityId): String? {
    val container = state.getEntity(entityId) ?: return null

    if (entityId in state.stack) {
        val faceDown = container.has<FaceDownComponent>() ||
            container.get<SpellOnStackComponent>()?.castFaceDown == true
        return if (faceDown) FACE_DOWN_DISPLAY_NAME else null
    }

    if (!container.has<FaceDownComponent>()) return null

    if (entityId in state.getBattlefield()) return FACE_DOWN_DISPLAY_NAME
    // Only phased-out permanents are missing from the memoized accessor above, so the unmemoized
    // physical-zone scan is reached only for them.
    if (container.has<PhasedOutComponent>() && entityId in state.allBattlefieldEntities()) {
        return FACE_DOWN_DISPLAY_NAME
    }

    val ownerId = container.get<CardComponent>()?.ownerId ?: container.get<OwnerComponent>()?.playerId
    if (ownerId != null && entityId in state.getExile(ownerId)) return FACE_DOWN_CARD_DISPLAY_NAME

    return null
}

/** The one player entitled to read [entityId]'s real name off the table (CR 708.5). */
private fun playerWhoMayLookAt(state: GameState, entityId: EntityId): EntityId? {
    val container = state.getEntity(entityId) ?: return null
    return state.projectedState.getController(entityId)
        ?: container.get<SpellOnStackComponent>()?.casterId
        ?: container.get<ControllerComponent>()?.playerId
        ?: container.get<CardComponent>()?.ownerId
        ?: container.get<OwnerComponent>()?.playerId
}
