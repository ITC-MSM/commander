package com.wingedsheep.engine.core

import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.replacement.GatheredReplacement
import com.wingedsheep.engine.replacement.PendingGameEvent
import com.wingedsheep.engine.replacement.ReplacementEffectIdentity
import com.wingedsheep.engine.replacement.ReplacementOutcome
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
 *
 * @property originalEvent The event that started the replacement chain
 * @property finalOutcome The final outcome after all replacements applied
 * @property originalContext The original execution context to resume with
 */
@Serializable
data class ReplacementResolveContinuation(
    override val decisionId: String,
    val originalEvent: PendingGameEvent? = null,
    val finalOutcome: ReplacementOutcome,
    val originalContext: EffectContext? = null
) : ContinuationFrame
