package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.normalize.Normalizer
import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.constant
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.ReplacementEffect

/**
 * "This land enters tapped." — the self-replacements a permanent applies to its own entry.
 *
 * ### Why a whole family for what looks like one sentence
 *
 * `EntersTapped` is one SDK type with three printed shapes, and the second of them costs nothing
 * once the first is written: the plain form, the shock-land form (`payLifeCost`), and the
 * check-land form (`unlessCondition`). The third is deliberately absent — an `unlessCondition` is
 * an arbitrary `Condition`, and the grammar has no condition vocabulary yet, so those 55 lines
 * decline and rank as the condition family's first customers rather than being approximated by the
 * plain rule. Printing an `EntersTapped` that carries a condition as "~ enters tapped." would be
 * the reversible-but-wrong class in its purest form: byte-perfect, and a different card.
 *
 * The `match` halves are equality tests against a reconstruction for exactly that reason, so a
 * value carrying a non-default `appliesTo` — a *static* tapped-entry imposed on other permanents,
 * which the SDK spells with the same type — refuses to print rather than claiming to be the
 * source's own line.
 *
 * ### No facade to build through
 *
 * Every other family here goes through an SDK companion facade, per the module's rule. Replacement
 * effects have none: `Effects`, `Triggers`, `Costs` and `Conditions` all exist, and hand-written
 * cards construct `EntersTapped(...)` directly (`replacementEffect(EntersTapped(payLifeCost = 2))`
 * on Steam Vents and Stomping Ground). So the constructor *is* the curated surface here, and the
 * missing `Replacements` facade is a small SDK finding rather than a rule this file should route
 * around.
 */
object Replacements {

    /** "~ enters tapped." — 234 hand-written cards, and every one of them the bare default. */
    private val entersTapped: Phrase<ReplacementEffect> =
        constant("${Normalizer.SELF} enters tapped.", EntersTapped())

    /**
     * "As ~ enters, you may pay 2 life. If you don't, it enters tapped." — the shock lands.
     *
     * The same type with one field set, which is why it is a row beside the plain rule rather than
     * a family of its own. The digit is [Primitives.cardinal] because Oracle spells a quantity of
     * life as a numeral, the convention [Steps] takes both leaves for.
     *
     * The template spells its second sentence mid-sentence ("if you don't") for the reason every
     * template here is written mid-sentence: a full stop is a sentence start, and
     * [com.wingedsheep.assay.syntax.SentenceCase] owns the capital at every one of them.
     */
    private val shockLand: Phrase<ReplacementEffect> = phrase(
        "as ${Normalizer.SELF} enters, you may pay {n} life. if you don't, it enters tapped.",
        name = "enters tapped unless you pay life",
    ) {
        slot("n", Primitives.cardinal)
        build { EntersTapped(payLifeCost = it.int("n")) }
        match { effect ->
            val life = (effect as? EntersTapped)?.payLifeCost ?: return@match null
            if (effect != EntersTapped(payLifeCost = life)) return@match null
            bind("n" to life)
        }
    }

    val replacement: Phrase<ReplacementEffect> = oneOf("a replacement effect", entersTapped, shockLand)
}
