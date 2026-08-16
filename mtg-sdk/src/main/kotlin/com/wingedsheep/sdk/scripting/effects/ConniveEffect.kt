package com.wingedsheep.sdk.scripting.effects

import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.text.TextReplacer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * "[subject] connives" (CR 701.50) — the named keyword action, wrapping the pipeline that carries
 * it out.
 *
 * Connive itself stays a composition: [body] is the ordinary Draw → Gather(hand) → Select(1) →
 * Move(Discard) → ConditionalOnCollection(Nonland) pipeline that
 * [com.wingedsheep.sdk.dsl.HandPatterns.connive] has always built, and the engine executes it
 * unchanged. This wrapper exists only to give that pipeline a *name and a subject*, which two
 * things need and a bare `Composite` cannot provide:
 *
 *  - **Replacement.** "If a creature you control would connive, instead …" (Leader, Super-Genius)
 *    is a [com.wingedsheep.sdk.scripting.ModifyKeywordAction] over
 *    [com.wingedsheep.sdk.scripting.EventPattern.ConnivedEvent]. The executor can only match it
 *    against the conniving permanent if it knows which permanent that is, hence [subject].
 *  - **Observation.** CR 701.50f: the permanent "connives" once the process completes, even if
 *    parts of it were impossible. The executor appends [EmitConnivedEventEffect] to [body] so the
 *    event lands after the discard decision resolves.
 *
 * This mirrors [ExploreEffect], the other keyword action with a replaceable, observable subject.
 *
 * Not every connive-*shaped* pipeline is a connive: Teo, Spirited Glider reads "draw a card, then
 * discard a card. When you discard a nonland card this way, put a +1/+1 counter on target creature
 * you control" and never says connive, so
 * [com.wingedsheep.sdk.dsl.HandPatterns.conniveTargeting] deliberately returns the bare pipeline
 * with no wrapper — it is not replaced by Leader and fires no connive triggers.
 *
 * @property subject The conniving permanent — the replacement's and the event's subject.
 * @property body The connive pipeline proper.
 * @property replacementsApplied Set on the re-issue after a [ModifyKeywordAction] prefix has been
 *   inserted, so a replacement never applies to its own replacement (CR 614.5). Never set in card
 *   data.
 */
@SerialName("Connive")
@Serializable
data class ConniveEffect(
    val subject: EffectTarget = EffectTarget.Self,
    val body: Effect,
    val replacementsApplied: Boolean = false
) : Effect {
    override val description: String = "${subject.description} connives"

    override fun applyTextReplacement(replacer: TextReplacer): Effect {
        val newBody = body.applyTextReplacement(replacer)
        return if (newBody !== body) copy(body = newBody) else this
    }
}

/**
 * Tail marker that emits the "a permanent connived" event (CR 701.50f), mirroring
 * [EmitExploredEventEffect].
 *
 * Appended by `ConniveEffectExecutor` as the last step of the connive pipeline rather than emitted
 * inline: connive pauses in the middle for the discard choice, and a game event emitted in a paused
 * batch does not reliably fire watcher triggers. Running as the pipeline's final step puts the
 * event in a completed resolution batch, after the discard and the +1/+1 counter.
 *
 * @property target The permanent that connived (the trigger's subject).
 */
@SerialName("EmitConnivedEvent")
@Serializable
data class EmitConnivedEventEffect(
    val target: EffectTarget = EffectTarget.Self
) : Effect {
    override val description: String = "${target.description} connived"
}
