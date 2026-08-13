package com.wingedsheep.engine.core

import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.replacement.GatheredReplacement
import com.wingedsheep.engine.replacement.PendingGameEvent
import com.wingedsheep.engine.replacement.ReplacementEffectIdentity
import com.wingedsheep.engine.handlers.effects.ZoneEntryOptions
import com.wingedsheep.engine.state.ZoneKey
import kotlinx.serialization.Serializable

/**
 * Continuation frame for when the player must choose between multiple
 * competing replacement effects that would all apply to the same event
 * (CR 616.1).
 *
 * When multiple replacement effects match the same [PendingGameEvent] and
 * all fall into the same priority group (CR 616.1a-d), the affected player
 * chooses which one to apply first. This frame captures everything needed
 * to resume after the choice.
 *
 * @property pendingEvent The event being replaced
 * @property options The competing replacement effects to choose from
 * @property alreadyApplied Effects already applied in this chain (CR 614.5)
 * @property context The execution context
 */
@Serializable
data class ReplacementChoiceContinuation(
    override val decisionId: String,
    val pendingEvent: PendingGameEvent,
    val options: List<GatheredReplacement>,
    val alreadyApplied: Set<ReplacementEffectIdentity>,
    val context: EffectContext? = null
) : ContinuationFrame

/** Resume an optional replacement after its affected player accepts or declines it. */
@Serializable
data class OptionalReplacementContinuation(
    override val decisionId: String,
    val pendingEvent: PendingGameEvent,
    val replacement: GatheredReplacement,
    val alreadyApplied: Set<ReplacementEffectIdentity>,
    val context: EffectContext? = null
) : ContinuationFrame

/** Performs the single zone transition left after a replacement chain settles. */
@Serializable
data class ZoneChangePerformContinuation(
    override val decisionId: String = "zone-change-perform",
    val entityId: com.wingedsheep.sdk.model.EntityId,
    val destination: com.wingedsheep.sdk.core.Zone,
    val options: ZoneEntryOptions,
    val fromZoneKey: ZoneKey,
    /** Optional rider of a replacement-with-effect, executed after the move. */
    val postMoveEffect: com.wingedsheep.sdk.scripting.effects.Effect? = null,
    val postMoveContext: EffectContext? = null,
    /** Source to link an exiled card to when a redirect says "exile it ...". */
    val linkExileToSourceId: com.wingedsheep.sdk.model.EntityId? = null
) : ContinuationFrame

/**
 * Runs a spell's final stack exit after a resolution effect that paused has
 * drained.  Omen needs this because its stack -> library move is subject to
 * the optional Commander replacement, but that replacement cannot displace an
 * earlier in-resolution choice.
 */
@Serializable
data class DeferredStackZoneMoveContinuation(
    override val decisionId: String = "deferred-stack-zone-move",
    val entityId: com.wingedsheep.sdk.model.EntityId,
    val ownerId: com.wingedsheep.sdk.model.EntityId,
    val destination: com.wingedsheep.sdk.core.Zone,
    val options: ZoneEntryOptions = ZoneEntryOptions()
) : ContinuationFrame

/**
 * Continuation frame for resuming the original execution context after a
 * replacement chain has fully resolved.
 *
 * When a replacement effect replaces an event with a new effect to execute
 * ([ReplacementOutcome.Replaced]), the new effect is pushed on the execution
 * stack. After it completes, this frame auto-resumes to carry the original
 * context forward so the caller can continue.
 *
 * This uses decisionId = "pending" for auto-resumption.
 */
@Serializable
data class ReplacementResolveContinuation(
    override val decisionId: String
) : ContinuationFrame
