package com.wingedsheep.assay.grammar

import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPlayer
import com.wingedsheep.sdk.scripting.targets.TargetRequirement

/**
 * Targeting, as the SDK splits it: a spell declares a [TargetRequirement] and its effect refers to
 * what was chosen through an [EffectTarget].
 *
 * The two halves are linked by a **name**, and the name is arbitrary — Ancestral Recall's golden
 * uses `"target"`, and any other string would behave identically. That makes it the one thing in a
 * parsed model that is not determined by the text, so the grammar always mints [SLOT] and the
 * differential renames both sides before comparing (see `Differential.normalizeSlotNames`). Two
 * models that differ only in what they called a slot are the same model, and neither the round trip
 * nor the differential should be able to see the difference.
 *
 * This file has no [com.wingedsheep.assay.syntax.Phrase] in it yet on purpose. A target is not a
 * line, and the phrasings that introduce one ("Target player draws…", "Destroy target creature")
 * are inseparable from the step that consumes it — English puts the verb and its object in one
 * clause, and so does the rule. What lives here is the vocabulary those rules share.
 */
object Targets {

    /**
     * The canonical name linking a [TargetRequirement] to the [EffectTarget] that reads it.
     *
     * One name is enough while every rule takes at most one target. When a rule needs two, this
     * becomes a generator and the differential's renaming already handles the rest.
     */
    const val SLOT = "target"

    /**
     * "target player" — the requirement half.
     *
     * Constructed directly rather than through `dsl.Targets.Player`, which is the facade for this
     * shape but exposes no id: it is a `val` fixed at `TargetPlayer()`. A requirement that cannot be
     * named cannot be referred to, so an effect wanting `EffectTarget.BoundVariable` has to bypass
     * it. Worth an `id` parameter on the facade; noted rather than changed here.
     */
    fun player(): TargetRequirement = TargetPlayer(id = SLOT)

    /** …and the reference half, for the effect that acts on it. */
    fun bound(): EffectTarget = EffectTarget.BoundVariable(SLOT)

    /** True when [target] is a reference to the single slot this grammar mints. */
    fun isBound(target: EffectTarget): Boolean =
        target is EffectTarget.BoundVariable && target.name == SLOT
}
