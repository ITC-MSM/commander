package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Bindings
import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.constant
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.assay.syntax.separated
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.effects.CreatePredefinedTokenEffect
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.effects.Effect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.TurnTracker
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * "Create a 1/1 green Insect creature token." — the token clauses.
 *
 * One shape with four slots (the count, the stats, the colours, the creature type) plus an optional
 * granted run, and a *row* per printed variation, because English changes several words at once: the
 * article and the noun's number move with the count, and the keyword rider is a suffix the kernel's
 * fixed templates cannot make optional. Six rows out of two axes is what the axes cost; nothing
 * about the token's own description is written twice.
 *
 * ### The colour word is a run, not [Primitives.color]
 *
 * A token's colours are a `Set<Color>`, which [Primitives.color] cannot spell: "colorless" is the
 * empty set rather than a colour, and "1/1 blue and red Otter" is two. So the slot is an alternation
 * over the empty set, one colour, and [Keywords.keywordRun]'s shape over colours — four alternatives
 * taking disjoint set *sizes*, which is what leaves printing determined by the model.
 *
 * Printing a set needs an order the model does not carry, and Magic's is not arbitrary: Oracle text
 * lists colours in WUBRG order on every card that names more than one, and [Color]'s own declaration
 * order is that order. So the printed run is the set sorted by ordinal, and a card that happened to
 * store its colours in another order still compares equal — sets have no order to disagree about.
 *
 * ### `imageUri` is not in the text, and the differential already knows it
 *
 * `CreateTokenEffect` carries an `imageUri` that no printed word determines — it is art, chosen when
 * the card was authored. The rules here build without one, and `Folds.dropPresentation` drops the
 * field from both sides before comparing, alongside `descriptionOverride`, `message` and `prompt`:
 * a parser can never produce a URL, so a card that inlines one would otherwise diverge for ever over
 * its picture while agreeing about its token.
 */
object Tokens {

    // ---------------------------------------------------------------------------------------
    // The colour run
    // ---------------------------------------------------------------------------------------

    /** WUBRG — [Color]'s declaration order, which is the order printed Oracle text uses. */
    private fun ordered(colours: Set<Color>): List<Color> = colours.sortedBy { it.ordinal }

    private val colourPair: Phrase<Set<Color>> =
        phrase("{first} and {second}", name = "two colours") {
            slot("first", Primitives.color)
            slot("second", Primitives.color)
            build { setOf(it.value<Color>("first"), it.value<Color>("second")) }
            match { colours ->
                colours.takeIf { it.size == 2 }?.let {
                    val order = ordered(it)
                    bind("first" to order[0], "second" to order[1])
                }
            }
        }

    private val colourSeries: Phrase<Set<Color>> =
        phrase("{most}, and {last}", name = "three or more colours") {
            slot("most", separated("colours", Primitives.color, ", ", min = 2))
            slot("last", Primitives.color)
            build { (it.value<List<Color>>("most") + it.value<Color>("last")).toSet() }
            match { colours ->
                colours.takeIf { it.size >= 3 }?.let {
                    val order = ordered(it)
                    bind("most" to order.dropLast(1), "last" to order.last())
                }
            }
        }

    private val colours: Phrase<Set<Color>> = oneOf(
        "a token's colours",
        listOf(
            constant("colorless", emptySet()),
            phrase<Set<Color>>("{one}", name = "one colour") {
                slot("one", Primitives.color)
                build { setOf(it.value<Color>("one")) }
                match { it.singleOrNull()?.let { only -> bind("one" to only) } }
            },
            colourPair,
            colourSeries,
        ),
    )

    // ---------------------------------------------------------------------------------------
    // Created creature tokens
    // ---------------------------------------------------------------------------------------

    /**
     * How many tokens a clause makes, as the two things that vary with it: the printed count and
     * the amount the model holds.
     *
     * A row rather than a slot because the noun's number changes with the count and the singular has
     * no number word at all — [Cardinals.word] starts at two for exactly that reason, so "a" and
     * "two" cannot be one slot without inventing a surface form for 1.
     */
    private class Count(
        val surface: String,
        val plural: Boolean,
        /** The count slot's phrase, for the counted form; null where the amount is fixed by the row. */
        val words: Phrase<Int>?,
        private val fixed: DynamicAmount?,
    ) {
        /** The amount a parse of this row denotes: the row's own, or the number word it just read. */
        fun amountFor(bindings: Bindings): DynamicAmount =
            fixed ?: DynamicAmount.Fixed(bindings.int("n"))

        /**
         * Is this the row that spells [amount]?
         *
         * The three rows are disjoint by construction — one is `Fixed(1)`, one is `XValue`, one is
         * any other spellable `Fixed` — so exactly one of them answers yes and printing is decided by
         * the model rather than by the list's order.
         */
        fun spells(amount: DynamicAmount): Boolean =
            if (words == null) amount == fixed
            else wordFor(amount)?.let(Cardinals::spellable) == true

        /** The number this row's slot would bind, or null on the rows that have no slot. */
        fun wordFor(amount: DynamicAmount): Int? =
            if (words == null) null else (amount as? DynamicAmount.Fixed)?.amount
    }

    private val counts: List<Count> = listOf(
        Count("a", plural = false, words = null, fixed = DynamicAmount.Fixed(1)),
        Count("{n}", plural = true, words = Cardinals.word, fixed = null),
        Count("X", plural = true, words = null, fixed = DynamicAmount.XValue),
    )

    /**
     * The shape: "create <count> P/T <colours> <type> creature token(s)[ with <keywords>]".
     *
     * The keyword rider builds the same list [Keywords.keywordRun] does everywhere else, into
     * `CreateTokenEffect.keywords` — one vocabulary for "gains flying and trample", "has flying and
     * trample" and "token with flying and trample", which is the whole reason that run is a shared
     * phrase rather than a rule inside one family.
     */
    private fun createToken(
        count: Count,
        keywords: Boolean,
        suffix: String = "",
        suffixName: String = "",
    ): Phrase<CardScript> {
        val noun = if (count.plural) "creature tokens" else "creature token"
        val rider = if (keywords) " with {kws}" else ""
        val name = "create " + (if (count.plural) "tokens" else "a token") +
            (if (keywords) " with keywords" else "") + suffixName

        fun scriptFor(
            amount: DynamicAmount,
            power: Int,
            toughness: Int,
            colours: Set<Color>,
            type: Subtype,
            granted: Set<Keyword>,
        ) = CardScript(
            spellEffect = Effects.CreateToken(
                count = amount,
                power = power,
                toughness = toughness,
                colors = colours,
                creatureTypes = setOf(type.value),
                keywords = granted,
            )
        )

        return phrase("create ${count.surface} {p}/{t} {color} {type} $noun$rider$suffix", name = name) {
            if (count.words != null) slot("n", count.words)
            slot("p", Primitives.cardinal)
            slot("t", Primitives.cardinal)
            slot("color", colours)
            slot("type", Primitives.subtype)
            if (keywords) slot("kws", Keywords.keywordRun)
            build { bindings ->
                val granted = if (keywords) bindings.value<List<Keyword>>("kws").toSet() else emptySet()
                scriptFor(
                    count.amountFor(bindings),
                    bindings.int("p"),
                    bindings.int("t"),
                    bindings.value("color"),
                    bindings.value("type"),
                    granted,
                )
            }
            match { script ->
                val token = script.spellEffect as? CreateTokenEffect ?: return@match null
                if (!count.spells(token.count)) return@match null
                if (keywords == token.keywords.isEmpty()) return@match null
                val type = token.creatureTypes.singleOrNull() ?: return@match null
                if (script != scriptFor(
                        token.count,
                        token.power,
                        token.toughness,
                        token.colors,
                        Subtype(type),
                        token.keywords,
                    )
                ) {
                    return@match null
                }
                bind(
                    "n" to count.wordFor(token.count),
                    "p" to token.power,
                    "t" to token.toughness,
                    "color" to token.colors,
                    "type" to Subtype(type),
                    "kws" to token.keywords.sortedBy { it.ordinal },
                )
            }
        }
    }

    // ---------------------------------------------------------------------------------------
    // Predefined tokens
    // ---------------------------------------------------------------------------------------

    /**
     * "Create a Food token.", "Create two Treasure tokens." — the tokens the rules define once and
     * every card names by their noun alone.
     *
     * The SDK holds these as `CreatePredefinedTokenEffect(tokenType)` rather than as a spelled-out
     * token, so the noun *is* the model and the row list is the vocabulary. It is deliberately the
     * set of nouns the SDK publishes a facade for: a name with no facade would be a string this
     * grammar invented, which is the one thing a rule building through the facades must not do.
     *
     * **The token noun is a proper noun.** It stands mid-sentence where [Subtype] words also do, and
     * `SentenceCase` has already lowercased the line's first letter, so the templates are written
     * exactly as printed and the capital is real rather than restored.
     *
     * ### A collision this file is deliberately one half of
     *
     * "Investigate" (CR 701.36a) *is* "create a Clue token" — `Effects.Investigate` and
     * `Effects.CreateClue` are the same call — so the two printed forms denote one model. Only the
     * noun form is registered here. The keyword-action spelling declines, which names the gap; what
     * it must never become is a second canonical rule, because then one model would have two printed
     * forms and nothing would decide which the printer emits. When the keyword-action family is
     * written, "investigate" belongs in it as an `alternate`.
     */
    private val PREDEFINED: List<Pair<String, (Int) -> Effect>> = listOf(
        "Treasure" to { n: Int -> Effects.CreateTreasure(count = n) },
        "Food" to { n: Int -> Effects.CreateFood(count = n) },
        "Clue" to { n: Int -> Effects.CreateClue(count = n) },
        "Blood" to { n: Int -> Effects.CreateBlood(count = n) },
        "Map" to { n: Int -> Effects.CreateMapToken(count = n) },
        "Lander" to { n: Int -> Effects.CreateLander(count = n) },
        "Shard" to { n: Int -> Effects.CreateShard(count = n) },
    )

    private fun createPredefined(count: Count, tokenType: String, effect: (Int) -> Effect): Phrase<CardScript> {
        val noun = if (count.plural) "tokens" else "token"
        fun scriptFor(amount: DynamicAmount): CardScript? {
            val fixed = (amount as? DynamicAmount.Fixed)?.amount ?: return null
            return CardScript(spellEffect = effect(fixed))
        }
        return phrase(
            "create ${count.surface} $tokenType $noun",
            name = "create ${if (count.plural) "$tokenType tokens" else "a $tokenType token"}",
        ) {
            if (count.words != null) slot("n", count.words)
            build { bindings -> scriptFor(count.amountFor(bindings)) }
            match { script ->
                val token = script.spellEffect as? CreatePredefinedTokenEffect ?: return@match null
                if (token.tokenType != tokenType) return@match null
                val amount = DynamicAmount.Fixed(token.count)
                if (!count.spells(amount)) return@match null
                if (script != scriptFor(amount)) return@match null
                bind("n" to count.wordFor(amount))
            }
        }
    }

    /** One token clause, for the sentences that wrap it — see [Granted]. */
    val clause: Phrase<CardScript> get() = oneOf("a token clause", clauses)

    val clauses: List<Phrase<CardScript>> =
        counts.flatMap { count -> listOf(createToken(count, keywords = false), createToken(count, keywords = true)) } +
            // Caller of the Claw. The tally is a *turn* tracker rather than a battlefield count, which
            // is why it is a row here and not one in [Amounts.count]: nothing about the phrase is a
            // noun the filter vocabulary could spell, and the whole clause names one tracked quantity.
            createToken(
                Count(
                    "a",
                    plural = false,
                    words = null,
                    fixed = DynamicAmount.TurnTracking(Player.You, TurnTracker.NONTOKEN_CREATURES_DIED),
                ),
                keywords = false,
                suffix = " for each nontoken creature put into your graveyard from the battlefield this turn",
                suffixName = " per creature that died this turn",
            ) +
            // "X Food tokens" is not printed — the predefined nouns take the article and the number
            // word only, so the X row is left out rather than written against nothing.
            PREDEFINED.flatMap { (type, effect) ->
                counts.dropLast(1).map { createPredefined(it, type, effect) }
            }
}
