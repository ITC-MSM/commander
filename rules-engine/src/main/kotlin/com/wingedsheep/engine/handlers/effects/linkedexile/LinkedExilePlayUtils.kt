package com.wingedsheep.engine.handlers.effects.linkedexile

import com.wingedsheep.engine.legality.LegalityKernel
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GrantMayCastFromLinkedExile
import com.wingedsheep.sdk.scripting.predicates.CardPredicate

/**
 * Shared logic for *playing lands* exiled with a permanent that has a
 * [GrantMayCastFromLinkedExile] static ability (e.g. Valgavoth, Terror Eater —
 * "During your turn, you may play cards exiled with Valgavoth").
 *
 * Casting *spells* from linked exile is handled by `CastFromZoneEnumerator.enumerateLinkedExile`
 * (which deliberately skips lands — most linked-exile granters say "cast spells"). Lands are a
 * separate play path: this helper lets `PlayLandEnumerator` surface them and `PlayLandHandler`
 * authorize them, but only for granters whose `filter` actually admits land cards.
 *
 * **The two paths share one [GrantMayCastFromLinkedExile.oncePerTurn] allowance.** Hauken's
 * Insight reads "Once during each of your turns, you may play a land **or** cast a spell from
 * among the cards exiled with this permanent" — one allowance for the permanent, spent by
 * whichever kind of play the controller makes, not one per play kind. Both paths ask
 * [LegalityKernel.linkedExileGranters], which applies the one `MayCastFromLinkedExileUsedThisTurnComponent`
 * gate, and `PlayLandHandler` stamps the same marker `CastSpellHandler` stamps. Which granter a play
 * spends is resolved the same way too — the first authorizing permanent in battlefield order — so
 * the two paths can never disagree about who paid.
 */
object LinkedExilePlayUtils {

    /** A linked-exile grant that currently lets [playerId] *play lands* from its pile. */
    data class LandGranter(val sourceId: EntityId, val ability: GrantMayCastFromLinkedExile, val exiledIds: List<EntityId>)

    /**
     * Every linked-exile grant [playerId] controls whose timing and once-per-turn allowance are
     * open right now, in battlefield order.
     *
     * Deliberately *not* filtered by the grant's card filter: whether a filter admits a land is a
     * question about a specific land, and answering it from the filter's shape alone is how the
     * Dinosaur-only pile ended up offering its lands. [landGranterFor] asks the filter about the
     * actual card instead, through [LegalityKernel.linkedExileAdmits] as the cast path does.
     */
    fun landGranters(state: GameState, playerId: EntityId, legality: LegalityKernel): List<LandGranter> =
        legality.linkedExileGranters(state, playerId)
            .map { LandGranter(it.granterId, it.ability, it.exiledIds) }

    /**
     * The grant [playerId] would be using to play [landCardId] from linked exile right now, or
     * null if none authorizes it.
     *
     * "Would be using" is the first authorizing permanent in battlefield order, mirroring
     * `CastZoneResolver.findLinkedExileGranterEntry`. `PlayLandHandler` calls this *before* the
     * land moves, because the move unlinks the card from its granter's pile and the answer is
     * gone afterwards.
     *
     * The filter is applied to the land itself rather than probed for a nonland predicate. Both
     * `GameObjectFilter.Nonland` and `GameObjectFilter.Creature` exclude a land card, but only the
     * first carries [CardPredicate.IsNonland] — so the shape probe this replaced let a land sitting
     * in a Dinosaur-only pile (Intrepid Paleontologist) be played, while the cast path with the
     * same filter refused it.
     */
    fun landGranterFor(
        state: GameState,
        playerId: EntityId,
        landCardId: EntityId,
        legality: LegalityKernel,
    ): LandGranter? {
        val card = state.getEntity(landCardId)?.get<CardComponent>() ?: return null
        if (!card.typeLine.isLand) return null
        val inExile = state.turnOrder.any { pid -> landCardId in state.getZone(ZoneKey(pid, Zone.EXILE)) }
        if (!inExile) return null
        return legality.linkedExileGranters(state, playerId)
            .firstOrNull { legality.linkedExileAdmits(state, playerId, it, landCardId) }
            ?.let { LandGranter(it.granterId, it.ability, it.exiledIds) }
    }

    /** True if [landCardId] is a land currently in exile that [playerId] may play via a linked-exile grant. */
    fun canPlayLand(state: GameState, playerId: EntityId, landCardId: EntityId, legality: LegalityKernel): Boolean =
        landGranterFor(state, playerId, landCardId, legality) != null
}
