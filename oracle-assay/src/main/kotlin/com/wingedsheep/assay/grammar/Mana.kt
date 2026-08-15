package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.assay.syntax.separated
import com.wingedsheep.assay.syntax.token
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.effects.AddColorlessManaEffect
import com.wingedsheep.sdk.scripting.effects.AddDynamicManaEffect
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.effects.AddManaOfChoiceEffect
import com.wingedsheep.sdk.scripting.effects.Effect
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Producing mana — "Add {G}.", "Add {C}{C}.", "Add {B} or {G}."
 *
 * The family exists because mana is the one effect Magic spells purely in symbols, and because the
 * *choice* form denotes several abilities rather than one effect with options. Both halves live
 * here so the word "add" is spelled in one file.
 *
 * ### `Add {B} or {G}.` is two abilities, and the SDK can say it two ways
 *
 * 165 hand-written cards spell a dual land's line as **two** `AddManaEffect` abilities sharing a
 * cost — Jungle Hollow's golden is two `CostTap` entries, one `BLACK` and one `GREEN` — and 13
 * spell it as one `AddManaOfChoiceEffect(ManaColorSet.Specific(...))`. Both work, and the split is
 * not arbitrary: every card in the second group carries a rider the first group cannot express
 * correctly ("Activate only once each turn" on two abilities would permit two activations), which
 * is a good reason for the type to exist and no reason for it to be a second spelling of the plain
 * case. The grammar therefore emits the majority form and never emits `ManaColorSet.Specific`, the
 * same treatment [Primitives.protectionScope] gives `ProtectionScope.Colors`. Registering both
 * would be genuine ambiguity — one text, two models — which the design says never to resolve by
 * picking one.
 *
 * A rule that denotes several things from one phrase is [Keywords.qualityRun]'s shape, which is why
 * [alternatives] hands back a list and [Activated] does the joining.
 */
object Mana {

    /**
     * "{G}", "{G}{G}", "{C}" — a repeated run of **one** mana symbol, as the effect it produces.
     *
     * One leaf rather than a symbol phrase plus a count, for the reason [Primitives.statModifiers]
     * is one leaf: the printed form repeats the symbol and the model holds a number, so neither
     * half can be written without seeing the other. A run of *different* symbols ("Add {W}{U}") is
     * a different effect the SDK spells as a composite, and this leaf declines it rather than
     * reading the first symbol and dropping the rest.
     *
     * The two halves are checked against each other by [token] itself, which re-reads what it
     * writes on every call — so an `AddManaEffect` carrying a restriction, a rider or a non-default
     * expiry prints "{G}", reads back as a plain one, compares unequal and refuses. Fail-closed by
     * construction rather than by a list of fields to remember.
     */
    val production: Phrase<Effect> = token(
        name = "mana symbols",
        pattern = Regex("""(?:\{[WUBRGC]})+"""),
        read = ::readProduction,
        write = ::writeProduction,
    )

    /**
     * "add {G}" — the clause, which is a spell effect in its own right (Dark Ritual).
     *
     * Periodless like every other clause in [Steps]: the full stop belongs to the sentence, not to
     * the verb phrase, which is what lets the same rule be a whole spell and the clause after an
     * activated ability's colon.
     */
    val addClause: Phrase<CardScript> = phrase("add {mana}", name = "add mana") {
        slot("mana", production)
        build { CardScript(spellEffect = it.value("mana")) }
        match { script ->
            val effect = script.spellEffect ?: return@match null
            if (script != CardScript(spellEffect = effect)) return@match null
            if (writeProduction(effect) == null) return@match null
            bind("mana" to effect)
        }
    }

    /**
     * "Add one mana of any color." — Blood Celebrant, and the whole any-colour family.
     *
     * The count is written as a *word* ("one mana", "two mana"), which is Oracle's convention for a
     * quantity of mana in prose as opposed to in symbols — so this takes [Cardinals.word] and the
     * singular is a rule of its own, the same split every counting rule in the grammar makes. There
     * is no plural of "mana", which is why only the noun before it changes.
     */
    private fun addAnyColour(template: String, name: String, fixed: Int?): Phrase<CardScript> {
        fun scriptFor(count: Int) = CardScript(spellEffect = Effects.AddAnyColorMana(count))
        return phrase(template, name = name) {
            if (fixed == null) slot("n", Cardinals.word)
            build { scriptFor(fixed ?: it.int("n")) }
            match { script ->
                val amount = (script.spellEffect as? AddManaOfChoiceEffect)?.amount
                    ?.let { it as? DynamicAmount.Fixed }?.amount ?: return@match null
                if (fixed != null && amount != fixed) return@match null
                if (fixed == null && !Cardinals.spellable(amount)) return@match null
                if (script != scriptFor(amount)) return@match null
                bind("n" to amount)
            }
        }
    }

    /**
     * "Add three mana in any combination of {R} and/or {G}." — Goblin Clearcutter.
     *
     * A different effect from [addAnyColour] and not a restriction on it: the colours are an
     * enumerated *set* the player draws from freely, so the model carries both the amount and the
     * set. Two colours is what every printed card in this shape names, and the join is the same
     * "and/or" [Filters.anyColour] reads — so a third colour is a row rather than a rule, whenever
     * one appears.
     */
    private val addCombination: Phrase<CardScript> = run {
        fun scriptFor(count: Int, first: Color, second: Color) = CardScript(
            spellEffect = Effects.AddDynamicMana(DynamicAmount.Fixed(count), setOf(first, second))
        )
        phrase(
            "add {n} mana in any combination of {first} and/or {second}",
            name = "add mana in any combination",
        ) {
            slot("n", Cardinals.word)
            slot("first", Primitives.manaSymbolColor)
            slot("second", Primitives.manaSymbolColor)
            build { scriptFor(it.int("n"), it.value("first"), it.value("second")) }
            match { script ->
                val effect = script.spellEffect as? AddDynamicManaEffect ?: return@match null
                val amount = (effect.amountSource as? DynamicAmount.Fixed)?.amount ?: return@match null
                val colours = effect.allowedColors.toList()
                if (colours.size != 2 || !Cardinals.spellable(amount)) return@match null
                if (script != scriptFor(amount, colours[0], colours[1])) return@match null
                bind("n" to amount, "first" to colours[0], "second" to colours[1])
            }
        }
    }

    /**
     * Every "add …" clause a spell or an ability can print, other than the symbol form above.
     *
     * Declared after the rules it lists — object initializers run in declaration order, and a `val`
     * reaching a later one reads a null out of a half-initialized object.
     */
    val addClauses: List<Phrase<CardScript>> = listOf(
        addAnyColour("add one mana of any color", "add one mana of any colour", fixed = 1),
        addAnyColour("add {n} mana of any color", "add several mana of any colour", fixed = null),
        addCombination,
    )

    /** "{B} or {G}" — exactly two, which is every dual land. */
    private val pair: Phrase<List<Effect>> = phrase("{first} or {second}", name = "two kinds of mana") {
        slot("first", production)
        slot("second", production)
        build { listOf(it.value("first"), it.value("second")) }
        match { effects -> effects.takeIf { it.size == 2 }?.let { bind("first" to it[0], "second" to it[1]) } }
    }

    /**
     * "{W}, {U}, or {B}" — three or more, with the Oxford comma the printed cards use. The same
     * two shapes [Primitives.scopeRun] is built from, over a different join word, and disjoint for
     * the same reason: [pair] takes exactly two and this takes at least three, so printing picks
     * the shape from the count rather than from a preference.
     */
    private val series: Phrase<List<Effect>> = phrase("{most}, or {last}", name = "three or more kinds of mana") {
        slot("most", separated("kinds of mana", production, ", ", min = 2))
        slot("last", production)
        build { it.value<List<Effect>>("most") + it.value<Effect>("last") }
        match { effects -> effects.takeIf { it.size >= 3 }?.let { bind("most" to it.dropLast(1), "last" to it.last()) } }
    }

    /** Two or more kinds of mana, joined the way printed Oracle text joins them. */
    private val alternatives: Phrase<List<Effect>> = oneOf("two or more kinds of mana", pair, series)

    /**
     * "Add {B} or {G}." — the sentence that denotes **several** mana effects, one per choice.
     *
     * Kept beside [added] rather than folded into it: one produces a `CardScript` because a single
     * mana effect is a spell effect a card can print on its own, and this produces a list of
     * effects because the choice form only ever appears as an activated ability's several
     * abilities. Their surface forms are disjoint — the join word is required here — so no text
     * reads both ways.
     */
    val addedAlternatives: Phrase<List<Effect>> = phrase("add {alternatives}.", name = "add one of several kinds of mana") {
        slot("alternatives", alternatives)
        build { it.value("alternatives") }
        match { effects ->
            if (effects.size < 2) return@match null
            if (effects.any { writeProduction(it) == null }) return@match null
            bind("alternatives" to effects)
        }
    }

    // -------------------------------------------------------------------------------------------
    // The leaf's two halves
    // -------------------------------------------------------------------------------------------

    /**
     * A colourless run is [AddColorlessManaEffect] and a coloured one is [AddManaEffect] — two SDK
     * types for one printed shape, which is why this reads the symbol before it reads the count.
     */
    private fun readProduction(symbols: String): Effect? {
        val letters = symbols.filter { it in "WUBRGC" }
        val symbol = letters.firstOrNull() ?: return null
        if (letters.any { it != symbol }) return null
        val count = letters.length
        if (symbol == 'C') return Effects.AddColorlessMana(count)
        return Effects.AddMana(Color.fromSymbol(symbol) ?: return null, count)
    }

    private fun writeProduction(effect: Effect): String? = when (effect) {
        is AddManaEffect -> effect.amount.fixed()?.let { "{${effect.color.symbol}}".repeat(it) }
        is AddColorlessManaEffect -> effect.amount.fixed()?.let { "{C}".repeat(it) }
        else -> null
    }

    private fun DynamicAmount.fixed(): Int? = (this as? DynamicAmount.Fixed)?.amount?.takeIf { it >= 1 }
}
