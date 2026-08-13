package com.wingedsheep.engine.core

import com.wingedsheep.engine.state.components.stack.EntitySnapshot
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.model.EntityId
import kotlinx.serialization.Serializable

/**
 * The selection-requiring additional-cost atoms a server-initiated free cast must still let the
 * caster pay. "Cast without paying its mana cost" waives only the *mana* cost (CR 601.2f /
 * 118.9); printed additional costs are paid as normal. Each kind maps 1:1 to a field of
 * [com.wingedsheep.sdk.scripting.AdditionalCostPayment].
 */
@Serializable
enum class AdditionalCostSelectionKind { SACRIFICE, DISCARD, EXILE, TAP, RETURN_TO_HAND }

/**
 * Pause/resume frame for choosing how to pay one selection-requiring additional cost during a
 * free cast (Roving Actuator / Shiko / Cascade copy-and-cast pipelines call
 * [com.wingedsheep.engine.handlers.actions.spell.CastSpellHandler.execute] directly with no
 * [com.wingedsheep.sdk.scripting.AdditionalCostPayment], so the selection isn't supplied by the
 * client the way a normal cast supplies it).
 *
 * On resume the chosen entities are merged into [baseCastAction]'s payment for [costKind], and
 * `execute()` is re-entered. The re-entry re-derives the unpaid costs and either pauses for the
 * next one or proceeds to pay — exactly mirroring the modal cast-time re-entry pattern
 * ([CastModalTargetSelectionContinuation]). The pause sits before any cost is paid, so it carries
 * no partial side effects.
 */
@Serializable
data class CastSpellAdditionalCostContinuation(
    override val decisionId: String,
    val cardId: EntityId,
    val casterId: EntityId,
    val baseCastAction: CastSpell,
    val costKind: AdditionalCostSelectionKind,
) : ContinuationFrame

/**
 * The part of a normal spell cast which follows a paid
 * [com.wingedsheep.sdk.scripting.costs.CostAtom.ReturnToHand] additional cost.
 *
 * This is deliberately not an action continuation.  Re-entering `CastSpellHandler.execute`
 * after a Commander replacement has moved a selected permanent would recalculate the cost
 * against a different battlefield and, much worse, pay every preceding additional cost again.
 * The frame therefore freezes the already-paid cost payload and the exact mana cost which was
 * fixed at CR 601.2f.  The auto-resumer invokes only the post-additional-cost tail.
 */
@Serializable
data class CastSpellPostAdditionalCostsContinuation(
    override val decisionId: String = "cast-spell-post-additional-costs",
    val action: CastSpell,
    val lockedEffectiveCost: ManaCost,
    val sacrificedSnapshots: List<EntitySnapshot> = emptyList(),
    val exiledCardCount: Int = 0,
    val beheldCards: List<EntityId> = emptyList(),
    val discardedAsCostCards: List<EntityId> = emptyList(),
    val chosenEntitySnapshots: List<EntitySnapshot> = emptyList(),
    val costPipelineCollections: Map<String, List<EntityId>> = emptyMap(),
    /** Cost events emitted before the replacement pause, in their original order. */
    val priorEvents: List<GameEvent> = emptyList(),
    /**
     * More permanents selected for this same ReturnToHand atom.  Each is attempted only after
     * the prior physical move has completed; the post-cost tail runs once this is empty.
     */
    val remainingPermanentIds: List<EntityId> = emptyList(),
    /** A Sneak/Web-slinging return already paid before a later ordinary additional-cost pause. */
    val alternativeBounceAlreadyPaid: Boolean = false,
    val sneakAttackDefenderId: EntityId? = null,
    val webSlungReturnedManaValue: Int = 0,
) : ContinuationFrame

/**
 * Frozen pre-payment tail for Sneak and Web-slinging's return-a-creature cost.
 *
 * The return is a component of the total cost, but Commander 903.9b can pause it.  We resume
 * the cast at the same payment point with the CR 601.2f cost locked; re-entering the public
 * action handler would otherwise recalculate that cost after the creature left and could pay a
 * different amount.  No other cost has been paid when this frame is made.
 */
@Serializable
data class CastSpellAlternativeBounceContinuation(
    override val decisionId: String = "cast-spell-alternative-bounce",
    val action: CastSpell,
    val lockedEffectiveCost: ManaCost,
    val wasSneaked: Boolean,
    val sneakAttackDefenderId: EntityId? = null,
    val wasWebSlung: Boolean,
    val webSlungReturnedManaValue: Int = 0,
    val priorEvents: List<GameEvent> = emptyList(),
) : ContinuationFrame
