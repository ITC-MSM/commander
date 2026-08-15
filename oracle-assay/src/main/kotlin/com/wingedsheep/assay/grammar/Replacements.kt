package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.normalize.Normalizer
import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.constant
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.scripting.ChoiceType
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.EntersWithChoice
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.ReplacementEffect
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

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

    /**
     * "As ~ enters, choose a color." — Ward Sliver, and the whole choose-as-it-enters family.
     *
     * A replacement rather than a triggered ability because "as … enters" happens *during* the
     * entry, not after it, which is what `EntersWithChoice` models. The kind of choice is a rule
     * parameter rather than a slot: each is a different English noun phrase ("a color", "a creature
     * type") rather than a different word in one, the same argument [Library.search] makes about its
     * destinations.
     */
    private fun entersWithChoice(noun: String, choice: ChoiceType): Phrase<ReplacementEffect> =
        constant("as ${Normalizer.SELF} enters, choose $noun.", EntersWithChoice(choice))

    /**
     * "~ enters with a +1/+1 counter on it.", "~ enters with three -1/-1 counters on it."
     *
     * ### Why `selfOnly` is spelled by the rule and not by a slot
     *
     * `EntersWithCounters` models both "this permanent enters with counters" and Hardened Scales'
     * "creatures you control enter with an extra counter" — the second is what its `appliesTo`
     * default describes, so the *self* reading is the one the flag has to state. The sentence says
     * "~ enters", naming the source and nothing else, so `selfOnly = true` is what this English
     * means; a value with `otherOnly`, a `condition` or a non-default `appliesTo` is a different
     * sentence and the reconstruct-and-compare refuses to print it. That last one matters here:
     * the kicker cards ("If ~ was kicked, it enters with two +1/+1 counters on it") carry a
     * `condition` and decline rather than losing the clause that makes them worth playing.
     *
     * ### The counter kind is a [CounterTypeFilter] here and a `String` on every effect
     *
     * Two SDK types for one concept, and `CounterTypeFilter.Named` can hold the same string the
     * dedicated cases do — so the grammar emits exactly one of the two spellings and reports the
     * other. [Primitives.counterFilter] and its inverse own that choice; the note is there.
     */
    private val entersWithCounters: List<Phrase<ReplacementEffect>> = run {
        fun effectFor(kind: String, count: Int): ReplacementEffect = EntersWithCounters(
            counterType = Primitives.counterFilter(kind),
            count = count,
            selfOnly = true,
        )
        fun rule(template: String, name: String, quantity: Phrase<*>?) =
            phrase(template, name = name) {
                slot("self", Primitives.self)
                slot("kind", if (quantity == null) Primitives.singularCounterKind else Primitives.counterKind)
                if (quantity != null) slot("n", quantity)
                build { effectFor(it.value("kind"), if (quantity == null) 1 else it.int("n")) }
                match { effect ->
                    val enters = effect as? EntersWithCounters ?: return@match null
                    val kind = Primitives.counterKindOf(enters.counterType) ?: return@match null
                    if (quantity == null && enters.count != 1) return@match null
                    if (quantity != null && !(enters.count >= 2 && Cardinals.spellable(enters.count))) {
                        return@match null
                    }
                    if (enters != effectFor(kind, enters.count)) return@match null
                    bind("self" to Unit, "kind" to kind, "n" to enters.count)
                }
            }
        listOf(
            rule("{self} enters with {kind} counter on it.", "enters with a counter", null),
            rule("{self} enters with {n} {kind} counters on it.", "enters with counters", Cardinals.word),
        )
    }

    val replacement: Phrase<ReplacementEffect> = oneOf(
        "a replacement effect",
        listOf(
            entersTapped,
            shockLand,
            entersWithChoice("a color", ChoiceType.COLOR),
            entersWithChoice("a creature type", ChoiceType.CREATURE_TYPE),
        ) + entersWithCounters,
    )
}
