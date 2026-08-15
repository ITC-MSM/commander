package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.constant
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.predicates.ControllerPredicate

/**
 * The noun phrase a spell acts on — "creature", "artifact or enchantment", "black creatures you
 * control", "creatures with power 2 or greater".
 *
 * The target of these rules is `mtg-sdk`'s [GameObjectFilter], which is a *bag of predicates*, and
 * that shape decides how the grammar has to be written. A predicate bag has no canonical spelling:
 * `Creature.youControl()` could in principle be printed by a rule for the type and a rule for the
 * controller in either order, and two rules that can each print part of one value are how a
 * bidirectional grammar goes underdetermined. So the rules here are **layered, not composed**: one
 * alternation spells the whole type phrase, and each layer around it owns exactly one field, strips
 * precisely that field, and delegates the rest inwards. Each layer also carries the layer below as a
 * pass-through alternative, which is what lets "black creatures you control" exist without a rule
 * that knows about both colour and control.
 *
 * ### The layers, innermost first
 *
 * | Layer | Owns | Surface |
 * |---|---|---|
 * | [typeNoun] | the whole predicate set of a named type | "creature", "nonbasic land", "Mountain" |
 * | [coloured] | the last [CardPredicate] when it is a colour one | "white creature", "nonblack creature" |
 * | [qualified] | the last [CardPredicate] when it is a keyword or power one | "creature with flying" |
 * | [controlledBy] | `controllerPredicate` | "creature you control" |
 *
 * The fluent builders these rules go through (`withColor`, `withKeyword`, `powerAtLeast`, …) all
 * **append** to `cardPredicates`, so the list is a stack and the outermost layer owns its top. That
 * is what makes "strip precisely the field I own" well-defined for a list-valued slot, and it is the
 * same order English uses: "white creature with flying" is built colour-then-keyword and printed
 * colour-then-keyword.
 *
 * ### Why the type list is enumerated
 *
 * "artifact or enchantment" is `Or([IsArtifact, IsEnchantment])` — an ordered list — while "artifact
 * creature" is two separate predicates. English does not distinguish them by shape, only by the
 * words, so deriving the surface from the predicates would need a theory of Magic's templating that
 * the SDK does not carry. Enumerating the printed forms keeps every rule provably invertible, and a
 * form nobody wrote down declines rather than being approximated — which is the point.
 *
 * ### Number is an axis, not a second vocabulary
 *
 * "Destroy target **creature**" and "Destroy all white **creatures**" name the same filters through
 * the same layers; only the type noun inflects. So the whole cascade is a function of grammatical
 * number and is instantiated twice — [filter] for the singular and [plural] for the plural — rather
 * than written twice. Everything outside the type noun ("you control", "with flying", "nonblack") is
 * number-invariant in Oracle-ese and is shared verbatim between the two.
 *
 * The plural of a *compound* type phrase is deliberately absent rather than derived: "artifact or
 * enchantment" pluralizes as "artifacts **and** enchantments", so the conjunction changes with the
 * number and nothing in the singular says so. Those rows carry no plural and decline in group
 * position, which names the gap instead of inventing a spelling.
 */
object Filters {

    /**
     * A type phrase and its plural, where English has one for it.
     *
     * Kept as a data row rather than as two lists so the two numbers of one type cannot drift apart,
     * and so a new type is one line in one place.
     */
    private data class TypeNoun(
        val singular: String,
        val plural: String?,
        val filter: GameObjectFilter,
    )

    private val TYPES: List<TypeNoun> = listOf(
        TypeNoun("creature", "creatures", GameObjectFilter.Creature),
        TypeNoun("artifact", "artifacts", GameObjectFilter.Artifact),
        TypeNoun("enchantment", "enchantments", GameObjectFilter.Enchantment),
        TypeNoun("land", "lands", GameObjectFilter.Land),
        TypeNoun("planeswalker", "planeswalkers", GameObjectFilter.Planeswalker),
        TypeNoun("permanent", "permanents", GameObjectFilter.Permanent),
        TypeNoun("nonland permanent", "nonland permanents", GameObjectFilter.NonlandPermanent),
        TypeNoun("noncreature permanent", "noncreature permanents", GameObjectFilter.NoncreaturePermanent),
        TypeNoun("basic land", "basic lands", GameObjectFilter.BasicLand),
        // The two card types that are never permanents. They appear in the same slot as the rest —
        // "target **sorcery** card in your graveyard", "search your library for an **instant**
        // card" — which is why they are rows here rather than a vocabulary of their own.
        TypeNoun("instant", "instants", GameObjectFilter.Instant),
        TypeNoun("sorcery", "sorceries", GameObjectFilter.Sorcery),
        TypeNoun("nonbasic land", "nonbasic lands", GameObjectFilter.NonbasicLand),
        TypeNoun("artifact creature", "artifact creatures", GameObjectFilter.ArtifactCreature),
        TypeNoun("creature or planeswalker", null, GameObjectFilter.CreatureOrPlaneswalker),
        TypeNoun("creature or enchantment", null, GameObjectFilter.CreatureOrEnchantment),
        // The plural swaps the conjunction: "creature or land" becomes "creatures **and** lands",
        // which is why a plural is a column in this table rather than a suffix rule.
        TypeNoun("creature or land", "creatures and lands", GameObjectFilter.CreatureOrLand),
        TypeNoun("artifact or enchantment", null, GameObjectFilter.ArtifactOrEnchantment),
        TypeNoun("artifact or land", null, GameObjectFilter.ArtifactOrLand),
        TypeNoun("attacking creature", "attacking creatures", GameObjectFilter.Creature.attacking()),
        TypeNoun("blocking creature", "blocking creatures", GameObjectFilter.Creature.blocking()),
        TypeNoun("tapped creature", "tapped creatures", GameObjectFilter.Creature.tapped()),
        TypeNoun("untapped creature", "untapped creatures", GameObjectFilter.Creature.untapped()),
    )

    /**
     * The basic land types, which stand in a type noun's slot with no "land" after them — "for each
     * **Mountain** target opponent controls".
     *
     * Generated from the SDK's own list rather than enumerated, because the CR defines the five as a
     * closed set and the SDK publishes it; and built as `Land.withSubtype(…)` because that is the
     * shape every hand-written card uses for them, the basic land types being land types.
     *
     * Only the *basic* land types are here. A capitalized word is a subtype of some kind, but which
     * card type it implies is not recoverable from the word — "Goblin" is a creature, "Equipment" an
     * artifact, "Gate" a land — and the SDK publishes no list to rank against for the latter two.
     * Guessing would be the reversible-but-wrong class again, so the rest decline.
     */
    private val BASIC_LAND_TYPES: List<TypeNoun> = Subtype.ALL_BASIC_LAND_TYPES.map { type ->
        // "Plains" is its own plural — the one invariant among the five, and the same trap
        // `Primitives.pluralSubtype` exists for. Appending an "s" would spell a type that is not one.
        TypeNoun(type, if (type.endsWith("s")) type else "${type}s", GameObjectFilter.Land.withSubtype(Subtype(type)))
    }

    private fun typeNoun(plural: Boolean): Phrase<GameObjectFilter> = oneOf(
        if (plural) "a permanent type (plural)" else "a permanent type",
        (TYPES + BASIC_LAND_TYPES).mapNotNull { noun ->
            (if (plural) noun.plural else noun.singular)?.let { constant(it, noun.filter) }
        },
    )

    // ---------------------------------------------------------------------------------------
    // The layers
    // ---------------------------------------------------------------------------------------

    /**
     * Strip the top of the predicate stack when it is the kind this layer owns.
     *
     * The pair is (the predicate, the filter without it) — the two halves a layer's `match` needs,
     * and the only place a layer is allowed to reach into `cardPredicates`.
     */
    private inline fun <reified P : CardPredicate> GameObjectFilter.stripTop(): Pair<P, GameObjectFilter>? {
        val top = cardPredicates.lastOrNull() as? P ?: return null
        return top to copy(cardPredicates = cardPredicates.dropLast(1))
    }

    /** "white creature" — one colour, as an adjective in front of the type noun. */
    private fun colour(inner: Phrase<GameObjectFilter>, name: String): Phrase<GameObjectFilter> =
        phrase("{color} {type}", name = name) {
            slot("color", Primitives.color)
            slot("type", inner)
            build { it.value<GameObjectFilter>("type").withColor(it.value("color")) }
            match { filter ->
                filter.stripTop<CardPredicate.HasColor>()
                    ?.let { (predicate, rest) -> bind("color" to predicate.color, "type" to rest) }
            }
        }

    /** "nonblack creature" — the negated colour, which Oracle writes as one word. */
    private fun notColour(inner: Phrase<GameObjectFilter>, name: String): Phrase<GameObjectFilter> =
        phrase("non{color} {type}", name = name) {
            slot("color", Primitives.color)
            slot("type", inner)
            build { it.value<GameObjectFilter>("type").notColor(it.value("color")) }
            match { filter ->
                filter.stripTop<CardPredicate.NotColor>()
                    ?.let { (predicate, rest) -> bind("color" to predicate.color, "type" to rest) }
            }
        }

    /**
     * "black and/or red creatures" — the disjunctive colour, which is one `Or` predicate rather
     * than two.
     *
     * "and/or" is the only join spelled here. "black or red creature" is a different printed form
     * for the same value and would be genuine ambiguity, so it declines; which of the two a card
     * prints is a templating choice the model has nowhere to keep.
     */
    private fun anyColour(inner: Phrase<GameObjectFilter>, name: String): Phrase<GameObjectFilter> =
        phrase("{first} and/or {second} {type}", name = name) {
            slot("first", Primitives.color)
            slot("second", Primitives.color)
            slot("type", inner)
            build {
                it.value<GameObjectFilter>("type")
                    .withAnyColor(it.value("first"), it.value("second"))
            }
            match { filter ->
                val (predicate, rest) = filter.stripTop<CardPredicate.Or>() ?: return@match null
                val colours = predicate.predicates.map { (it as? CardPredicate.HasColor)?.color ?: return@match null }
                if (colours.size != 2) return@match null
                bind("first" to colours[0], "second" to colours[1], "type" to rest)
            }
        }

    /** "creatures with flying" — a keyword the members must have. */
    private fun withKeyword(inner: Phrase<GameObjectFilter>, name: String): Phrase<GameObjectFilter> =
        phrase("{type} with {kw}", name = name) {
            slot("type", inner)
            slot("kw", Keywords.keyword)
            build { it.value<GameObjectFilter>("type").withKeyword(it.value<Keyword>("kw")) }
            match { filter ->
                filter.stripTop<CardPredicate.HasKeyword>()
                    ?.let { (predicate, rest) -> bind("type" to rest, "kw" to predicate.keyword) }
            }
        }

    /** "creatures without flying" — the keyword layer's negation. */
    private fun withoutKeyword(inner: Phrase<GameObjectFilter>, name: String): Phrase<GameObjectFilter> =
        phrase("{type} without {kw}", name = name) {
            slot("type", inner)
            slot("kw", Keywords.keyword)
            build { it.value<GameObjectFilter>("type").withoutKeyword(it.value<Keyword>("kw")) }
            match { filter ->
                filter.stripTop<CardPredicate.NotKeyword>()
                    ?.let { (predicate, rest) -> bind("type" to rest, "kw" to predicate.keyword) }
            }
        }

    /** "creatures with power 2 or greater". */
    private fun withPowerAtLeast(inner: Phrase<GameObjectFilter>, name: String): Phrase<GameObjectFilter> =
        phrase("{type} with power {n} or greater", name = name) {
            slot("type", inner)
            slot("n", Primitives.cardinal)
            build { it.value<GameObjectFilter>("type").powerAtLeast(it.int("n")) }
            match { filter ->
                filter.stripTop<CardPredicate.PowerAtLeast>()
                    ?.let { (predicate, rest) -> bind("type" to rest, "n" to predicate.min) }
            }
        }

    /** "creatures with power 2 or less". */
    private fun withPowerAtMost(inner: Phrase<GameObjectFilter>, name: String): Phrase<GameObjectFilter> =
        phrase("{type} with power {n} or less", name = name) {
            slot("type", inner)
            slot("n", Primitives.cardinal)
            build { it.value<GameObjectFilter>("type").powerAtMost(it.int("n")) }
            match { filter ->
                filter.stripTop<CardPredicate.PowerAtMost>()
                    ?.let { (predicate, rest) -> bind("type" to rest, "n" to predicate.max) }
            }
        }

    /**
     * The controller clause, which is a suffix in English and a single field in the model — so it is
     * one rule per printed form, each stripping [GameObjectFilter.controllerPredicate] and handing
     * the rest back inwards.
     */
    private fun controlledBy(
        inner: Phrase<GameObjectFilter>,
        surface: String,
        predicate: ControllerPredicate,
        name: String,
    ): Phrase<GameObjectFilter> =
        phrase("{type} $surface", name = name) {
            slot("type", inner)
            build { it.value<GameObjectFilter>("type").copy(controllerPredicate = predicate) }
            match { filter ->
                if (filter.controllerPredicate != predicate) {
                    null
                } else {
                    bind("type" to filter.copy(controllerPredicate = null))
                }
            }
        }

    /**
     * The whole cascade, for one grammatical number.
     *
     * Each level carries the level below as its first alternative, so a filter that uses none of a
     * layer's vocabulary is printed by the layer that owns what it *does* use. Printing stays
     * determined by the model rather than by alternation order, because every rule's `match` tests
     * the exact field it owns and the type nouns are exact values.
     */
    private fun nounPhrase(plural: Boolean): Phrase<GameObjectFilter> {
        val suffix = if (plural) " (plural)" else ""
        val types = typeNoun(plural)
        val coloured = oneOf(
            "a coloured permanent$suffix",
            types,
            colour(types, "a coloured permanent$suffix"),
            notColour(types, "a permanent of another colour$suffix"),
            anyColour(types, "a permanent of either colour$suffix"),
        )
        val qualified = oneOf(
            "a qualified permanent$suffix",
            coloured,
            withKeyword(coloured, "a permanent with a keyword$suffix"),
            withoutKeyword(coloured, "a permanent without a keyword$suffix"),
            withPowerAtLeast(coloured, "a permanent with power at least$suffix"),
            withPowerAtMost(coloured, "a permanent with power at most$suffix"),
        )
        return oneOf(
            "a permanent$suffix",
            qualified,
            controlledBy(qualified, "you control", ControllerPredicate.ControlledByYou, "a permanent you control$suffix"),
            controlledBy(
                qualified,
                "an opponent controls",
                ControllerPredicate.ControlledByOpponent,
                "a permanent an opponent controls$suffix",
            ),
        )
    }

    /** A whole noun phrase in the singular — "creature", "nonblack attacking creature". */
    val filter: Phrase<GameObjectFilter> = nounPhrase(plural = false)

    /** …and in the plural — "creatures you control", "creatures with power 2 or greater". */
    val plural: Phrase<GameObjectFilter> = nounPhrase(plural = true)

    /**
     * "a Forest", "an Island", "a creature" — a singular noun phrase with its indefinite article.
     *
     * The article is not in the model and never will be: English derives it from the *spelling* of
     * the word that follows, which the filter's printed form supplies. So both halves of both rules
     * derive it from the same function over [filter]'s own output, and the rule whose article
     * disagrees refuses in **both** directions — "an Forest" fails to parse for exactly the reason
     * it fails to print. That is what keeps one printed form per model with two alternatives in the
     * alternation, and it is why this is a pair of rules rather than a leaf: the noun inside is a
     * whole layered phrase, which a [com.wingedsheep.assay.syntax.token] cannot slot.
     */
    val indefinite: Phrase<GameObjectFilter> = oneOf(
        "a permanent with its article",
        article("a"),
        article("an"),
    )

    private fun article(article: String): Phrase<GameObjectFilter> =
        phrase("$article {type}", name = "\"$article\" plus a permanent") {
            slot("type", filter)
            build { it.value<GameObjectFilter>("type").takeIf { f -> articleFor(f) == article } }
            match { f -> if (articleFor(f) == article) bind("type" to f) else null }
        }

    /** The article [filter] would print [f] with, or null when it cannot print it at all. */
    private fun articleFor(f: GameObjectFilter): String? {
        val head = filter.unparse(f)?.firstOrNull()?.lowercaseChar() ?: return null
        return if (head in listOf('a', 'e', 'i', 'o', 'u')) "an" else "a"
    }
}
