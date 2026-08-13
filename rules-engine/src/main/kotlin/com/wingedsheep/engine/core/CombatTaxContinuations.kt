package com.wingedsheep.engine.core

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.model.EntityId
import kotlinx.serialization.Serializable

/**
 * Resume after the attacking player selects which mana sources to tap for an attack
 * tax (Propaganda, Ghostly Prison, Windborn Muse, Collective Restraint, etc.).
 *
 * The engine pauses *before* tapping anything and shows a [SelectManaSourcesDecision]
 * with the auto-pay suggestion pre-selected, mirroring the cast / counter-unless-pays
 * UX. On confirm the resumer taps the selected sources (or runs `ManaSolver.solve` when
 * `autoPay = true`) and commits the attack declaration. An empty manual selection
 * (`autoPay = false` + `selectedSources = []`) is treated as "cancel attack" — a clean
 * no-op that leaves the player in `DECLARE_ATTACKERS`.
 *
 * @property attackingPlayer Player who declared the attack.
 * @property attackers Original [attacker → defender] map from the [DeclareAttackers] action.
 * @property manaCost Total tax to pay, encoded as a generic-mana cost.
 * @property availableSources Mana sources the player chooses from in the prompt.
 * @property autoPaySuggestion Pre-computed entity IDs the solver would tap on auto-pay.
 * @property bands Validated band groupings (CR 702.22) carried across the tax pause so the
 *   resumer can stamp band ids when it commits the attack.
 */
@Serializable
data class AttackTaxManaSelectionContinuation(
    override val decisionId: String,
    val attackingPlayer: EntityId,
    val attackers: Map<EntityId, EntityId>,
    val manaCost: ManaCost,
    val availableSources: List<ManaSourceOption>,
    val autoPaySuggestion: List<EntityId>,
    val bands: List<Set<EntityId>> = emptyList(),
) : ContinuationFrame

/** A single defending player's immutable share of a combined block-tax payment. */
@Serializable
data class BlockTaxPayerPlan(
    val payerId: EntityId,
    val manaCost: ManaCost,
    val availableSources: List<ManaSourceOption>,
    val autoPaySuggestion: List<EntityId>,
)

/**
 * A payer's accepted intent. Collecting an intent never taps a permanent or spends mana; all
 * intents are applied to a candidate state only after every payer has accepted.
 */
@Serializable
data class BlockTaxPaymentIntent(
    val payerId: EntityId,
    val selectedSources: List<EntityId> = emptyList(),
    val autoPay: Boolean = false,
)

/**
 * Block-tax mirror of [AttackTaxManaSelectionContinuation]. A normal declaration has one
 * [payerPlans] entry. A shared-team declaration carries one immutable plan per controller and
 * collects their [acceptedIntents] without changing game state. Only the final resume applies all
 * payments and commits the one combined declaration.
 *
 * @property blockingPlayer Representative who submitted the combined declaration.
 * @property blockers Original [blocker → attackers it blocks] map from [DeclareBlockers].
 * @property payerPlans Fixed costs and available simple mana sources, in prompt order.
 * @property payerIndex Index of the payer whose decision owns [decisionId].
 * @property acceptedIntents Zero-mutation payment intents accepted by earlier payers.
 */
@Serializable
data class BlockTaxManaSelectionContinuation(
    override val decisionId: String,
    val blockingPlayer: EntityId,
    val blockers: Map<EntityId, List<EntityId>>,
    val payerPlans: List<BlockTaxPayerPlan> = emptyList(),
    val payerIndex: Int = 0,
    val acceptedIntents: List<BlockTaxPaymentIntent> = emptyList(),
    // Kept solely to deserialize a replay/save paused by the pre-atomic continuation schema.
    // The resumer turns these fields into one legacy payer plan and executes the old one-payer
    // payment path; newly created continuations leave all three at their defaults.
    @Deprecated("Only for backward-compatible continuation deserialization")
    val manaCost: ManaCost? = null,
    @Deprecated("Only for backward-compatible continuation deserialization")
    val availableSources: List<ManaSourceOption> = emptyList(),
    @Deprecated("Only for backward-compatible continuation deserialization")
    val autoPaySuggestion: List<EntityId> = emptyList(),
) : ContinuationFrame
