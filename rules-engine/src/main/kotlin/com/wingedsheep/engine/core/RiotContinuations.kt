package com.wingedsheep.engine.core

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import kotlinx.serialization.Serializable

/**
 * Resume after a player answers one instance of **riot** (CR 702.136) for a permanent spell that is
 * still resolving — the entering object is the spell entity, not yet on the battlefield.
 *
 * [remainingInstances] is how many *further* riot instances this permanent still owes a choice
 * (CR 702.136b — each instance works separately). The resumer applies the answered instance, chains
 * to the next one, and once none are left finishes the permanent's entry exactly the way
 * [EntersWithChoiceSpellContinuation]'s resumer does (place it on the battlefield, emit the
 * resolution + entry events).
 *
 * @property spellId The resolving permanent spell
 * @property controllerId The player making the riot choices
 * @property ownerId The owner of the card
 */
@Serializable
data class RiotEntrySpellContinuation(
    override val decisionId: String,
    val spellId: EntityId,
    val controllerId: EntityId,
    val ownerId: EntityId,
    val remainingInstances: Int,
) : ContinuationFrame

/**
 * Resume after a player answers one instance of **riot** for a permanent that entered the
 * battlefield **directly** — reanimated, tutored straight into play, or minted as a token copy.
 * The permanent is already on the battlefield; only the riot choice is outstanding.
 *
 * The entry [ZoneChangeEvent] is deliberately *withheld* by the caller and re-emitted here once the
 * last instance resolves, so enters-the-battlefield triggers see the permanent with its riot
 * counter already on it (and never fire twice — the same contract
 * [EntersWithChoiceOnBattlefieldContinuation] documents).
 *
 * [remainingEntities] carries the rest of a multi-permanent entry batch (a MoveCollection putting
 * several creatures onto the battlefield at once): each is walked in turn, mirroring the
 * `remainingAuras` pass the same executor already uses for "choose what this Aura enchants".
 *
 * @property entityId The permanent already on the battlefield whose riot is being answered
 * @property controllerId The player making the riot choices
 * @property remainingInstances Further riot instances owed by [entityId] (CR 702.136b)
 * @property remainingEntities Further entered permanents in this batch that still owe riot choices
 * @property fromZone The zone [entityId] came from, used to synthesize its entry event
 */
@Serializable
data class RiotEntryOnBattlefieldContinuation(
    override val decisionId: String,
    val entityId: EntityId,
    val controllerId: EntityId,
    val remainingInstances: Int,
    val remainingEntities: List<EntityId> = emptyList(),
    val fromZone: Zone? = null,
) : ContinuationFrame
