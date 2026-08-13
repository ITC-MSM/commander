package com.wingedsheep.engine.core

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.costs.PayCost
import com.wingedsheep.sdk.scripting.costs.CostAtom
import com.wingedsheep.sdk.scripting.effects.Effect
import com.wingedsheep.sdk.scripting.targets.TargetRequirement
import com.wingedsheep.engine.state.components.stack.ActivatedAbilityOnStackComponent
import kotlinx.serialization.Serializable

/**
 * Resume after a player responds to a cost-payment prompt created by
 * `com.wingedsheep.engine.mechanics.cost.CostPaymentService.pay`.
 *
 * A *single* frame serves all ten [PayCost] variants: the resumer dispatches on [cost]'s shape to
 * interpret the response — yes/no for mana / life / random-discard, card-selection for
 * discard / exile / reveal / sacrifice / return / tap, option-pick for [PayCost.Choice]. On a paid
 * outcome it performs the payment and runs [onPaid]; on a declined or short selection it runs
 * [onDeclined]. Either way it then calls `checkForMore`, so a caller-pushed frame beneath this one
 * resumes for non-effect follow-ups.
 *
 * [PayCost.OwnManaCost] is resolved to a concrete [PayCost.Atom] (CostAtom.Mana) (against the source's printed cost)
 * before the frame is created, so it never appears here. A [PayCost.Choice] is stored already reduced
 * to the *affordable* options, so the chosen option index maps positionally onto [PayCost.Choice.options]
 * and an index past the end means "decline".
 *
 * @property payerId the player paying the cost.
 * @property sourceId the spell/permanent the cost is attached to (needed for self-exclusion and
 *   own-mana-cost resolution).
 * @property sourceName name of the source for event/prompt text.
 * @property cost the (already-resolved) cost being paid.
 * @property onPaid effect to run after the cost is paid (null = nothing).
 * @property onDeclined effect to run if the cost is declined or unpayable (null = nothing).
 * @property targets / [namedTargets] / [storedCollections] context threaded into the follow-up so it
 *   can resolve `ContextTarget`s and reference pipeline collections.
 */
@Serializable
data class CostPaymentContinuation(
    override val decisionId: String,
    val payerId: EntityId,
    val sourceId: EntityId,
    val sourceName: String,
    val cost: PayCost,
    val onPaid: Effect? = null,
    val onDeclined: Effect? = null,
    val targets: List<ChosenTarget> = emptyList(),
    val namedTargets: Map<String, ChosenTarget> = emptyMap(),
    val storedCollections: Map<String, List<EntityId>> = emptyMap()
) : ContinuationFrame

/**
 * Resume after the payer picks mana sources for a [PayCost.Atom] mana cost they already agreed to
 * pay through [CostPaymentContinuation]'s yes/no.
 *
 * Costs paid via `CostPaymentService` used to go straight from "Pay {2}?" to the auto-tap solver,
 * which meant the payer had no say in which permanents were tapped and no chance to activate a mana
 * ability (CR 605.3a). Worse, `canAfford` counts mana the solver won't auto-tap — a Treasure, an
 * Ashnod's Altar — so "yes" could be accepted and then silently fail. This frame inserts the same
 * second step ward and "counter unless you pay" have always had.
 *
 * [inner] carries the original payment untouched: once the chosen sources are tapped and their mana
 * is in the pool, the resumer performs [CostPaymentContinuation.cost] and runs its `onPaid` /
 * `onDeclined` follow-up exactly as the direct path would.
 */
@Serializable
data class CostPaymentManaSelectionContinuation(
    override val decisionId: String,
    val inner: CostPaymentContinuation,
    val manaCost: com.wingedsheep.sdk.core.ManaCost,
    val availableSources: List<ManaSourceOption>
) : ContinuationFrame

/**
 * Remainder of a [CostAtom.ReturnToHand] payment after a zone-change
 * replacement paused it.  It sits below the replacement continuation, so the
 * physical move happens first and this frame then resumes the remaining
 * payment exactly once.
 *
 * A return-to-hand payment is not allowed to use the synchronous movement
 * shortcut: a Commander can choose the command zone instead before it moves.
 */
@Serializable
data class CostPaymentReturnToHandContinuation(
    override val decisionId: String = "cost-payment-return-to-hand",
    val originalPayment: CostPaymentContinuation,
    val remainingPermanentIds: List<EntityId>,
    val priorEvents: List<GameEvent> = emptyList()
) : ContinuationFrame

/**
 * The tail of an activated ability whose final cost atom paused for a Commander
 * hand replacement.  Cost payment has already completed; this frame must only
 * put the already-validated ability on the stack, never re-enter activation.
 */
@Serializable
data class ActivatedAbilityPostPaymentContinuation(
    override val decisionId: String = "activated-ability-post-payment",
    val controllerId: EntityId,
    val abilityOnStack: ActivatedAbilityOnStackComponent,
    val targets: List<ChosenTarget>,
    val targetRequirements: List<TargetRequirement>,
    val costsTap: Boolean,
    val isExhaust: Boolean,
    val cantBeCopied: Boolean,
    /** Events already emitted while paying costs; used for trigger detection once. */
    val costEvents: List<GameEvent>
) : ContinuationFrame

/** Remaining selected permanents in an activation's ReturnToHand cost. */
@Serializable
data class ActivatedAbilityReturnToHandRemainderContinuation(
    override val decisionId: String = "activated-ability-return-to-hand-remainder",
    val tail: ActivatedAbilityPostPaymentContinuation,
    val remainingPermanentIds: List<EntityId>
) : ContinuationFrame
