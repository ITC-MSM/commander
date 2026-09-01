package com.wingedsheep.engine.handlers.effects.linkedexile

import com.wingedsheep.engine.handlers.actions.spell.CastZoneResolver
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.LinkedExileComponent
import com.wingedsheep.engine.state.components.battlefield.MayCastFromLinkedExileUsedThisTurnComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
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
 * whichever kind of play the controller makes, not one per play kind. So this file applies the
 * same [MayCastFromLinkedExileUsedThisTurnComponent] gate `CastZoneResolver
 * .findLinkedExileGranterEntry` applies, and `PlayLandHandler` stamps the same marker
 * `CastSpellHandler` stamps. Which granter a play spends is resolved the same way too — the first
 * authorizing permanent in battlefield order — so the two paths can never disagree about who paid.
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
     * actual card instead, through the same matcher the cast path uses.
     */
    fun landGranters(state: GameState, playerId: EntityId, cardRegistry: CardRegistry): List<LandGranter> {
        val result = mutableListOf<LandGranter>()
        for (entityId in state.getBattlefield()) {
            val container = state.getEntity(entityId) ?: continue
            if (container.get<ControllerComponent>()?.playerId != playerId) continue
            val linked = container.get<LinkedExileComponent>() ?: continue
            val cardDef = container.get<CardComponent>()?.let { cardRegistry.getCard(it.cardDefinitionId) } ?: continue
            val grant = cardDef.script.staticAbilities.filterIsInstance<GrantMayCastFromLinkedExile>().firstOrNull() ?: continue
            // Timing — "during your turn" grants only let you play lands on your own turn.
            if (grant.duringYourTurnOnly && !state.isActiveTurnFor(playerId)) continue
            // Once-per-turn allowance already spent this turn — by a cast or by an earlier land
            // play, since both spend the same marker.
            if (grant.oncePerTurn &&
                container.get<MayCastFromLinkedExileUsedThisTurnComponent>() != null
            ) continue
            result.add(LandGranter(entityId, grant, linked.exiledIds))
        }
        return result
    }

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
        cardRegistry: CardRegistry,
    ): LandGranter? {
        val card = state.getEntity(landCardId)?.get<CardComponent>() ?: return null
        if (!card.typeLine.isLand) return null
        val inExile = state.turnOrder.any { pid -> landCardId in state.getZone(ZoneKey(pid, Zone.EXILE)) }
        if (!inExile) return null
        return landGranters(state, playerId, cardRegistry).firstOrNull { granter ->
            landCardId in granter.exiledIds &&
                (!granter.ability.ownedByYou || card.ownerId == playerId) &&
                CastZoneResolver.matchesCardFilter(card, granter.ability.filter, state, granter.sourceId)
        }
    }

    /** True if [landCardId] is a land currently in exile that [playerId] may play via a linked-exile grant. */
    fun canPlayLand(state: GameState, playerId: EntityId, landCardId: EntityId, cardRegistry: CardRegistry): Boolean =
        landGranterFor(state, playerId, landCardId, cardRegistry) != null
}
