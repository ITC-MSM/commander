package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.alternate
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
        // A bare quality with no noun of its own: "Noncreature spells cost {1} more to cast." puts
        // it in front of a word ("spells") that is not a permanent type, so the filter is the
        // adjective alone and does not inflect.
        TypeNoun("noncreature", "noncreature", GameObjectFilter.Noncreature),
        TypeNoun("artifact creature", "artifact creatures", GameObjectFilter.ArtifactCreature),
        TypeNoun("creature or planeswalker", null, GameObjectFilter.CreatureOrPlaneswalker),
        TypeNoun("creature or enchantment", null, GameObjectFilter.CreatureOrEnchantment),
        // The plural swaps the conjunction: "creature or land" becomes "creatures **and** lands",
        // which is why a plural is a column in this table rather than a suffix rule.
        TypeNoun("creature or land", "creatures and lands", GameObjectFilter.CreatureOrLand),
        TypeNoun("artifact or enchantment", null, GameObjectFilter.ArtifactOrEnchantment),
        TypeNoun("artifact or land", null, GameObjectFilter.ArtifactOrLand),
        TypeNoun("attacking creature", "attacking creatures", GameObjectFilter.Creature.attacking()),
        // A `StatePredicate.Or` of the two, which is one printed phrase and one value — the same
        // shape as the "artifact or enchantment" row above, and enumerated for the same reason.
        TypeNoun(
            "attacking or blocking creature",
            "attacking or blocking creatures",
            GameObjectFilter.Creature.attackingOrBlocking(),
        ),
        TypeNoun("face-down creature", "face-down creatures", GameObjectFilter.Creature.faceDown()),
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
    // Subtypes — the tribal adjective, and the bare noun that stands for it
    // ---------------------------------------------------------------------------------------

    /**
     * "Sliver creature", "Goblin permanents" — a subtype in front of a type noun.
     *
     * The layer sits *inside* [colour] and the rest rather than outside them, because that is the
     * order both English and the predicate stack use: "black Sliver creature" builds the subtype
     * first and the colour on top, so the colour layer owns the top of the stack and this one owns
     * what is under it. Putting it further out would print "Sliver black creature".
     *
     * The subtype leaf is **ungated**: the card type comes from the noun this modifies, so nothing
     * is being guessed and there is no candidate to rank — unlike the bare form below, where the
     * word alone has to imply "creature". [Primitives.pluralSubtype]'s ranking exists for the
     * de-pluralization, which this layer never performs.
     *
     * The adjective stays **singular in both numbers** — "Sliver creature" and "Sliver creatures" —
     * because only the head noun inflects in English. This layer therefore takes no number
     * parameter, which is why it sits inside the cascade rather than being instantiated twice.
     */
    private fun subtyped(inner: Phrase<GameObjectFilter>, name: String): Phrase<GameObjectFilter> =
        phrase("{subtype} {type}", name = name) {
            slot("subtype", Primitives.subtype)
            slot("type", inner)
            build { it.value<GameObjectFilter>("type").withSubtype(it.value<Subtype>("subtype")) }
            match { filter ->
                filter.stripTop<CardPredicate.HasSubtype>()
                    ?.let { (predicate, rest) -> bind("subtype" to predicate.subtype, "type" to rest) }
            }
        }

    /**
     * "Bird and/or Cleric permanent" — two subtypes, either of which qualifies.
     *
     * One `Or` predicate rather than two, exactly as [anyColour] reads the colour disjunction, and
     * "and/or" is the only join spelled for the same reason: "Bird or Cleric permanent" would be a
     * second printed form for one value with nothing for the printer to choose.
     */
    private fun anySubtype(inner: Phrase<GameObjectFilter>, name: String): Phrase<GameObjectFilter> =
        phrase("{first} and/or {second} {type}", name = name) {
            slot("first", Primitives.subtype)
            slot("second", Primitives.subtype)
            slot("type", inner)
            build {
                it.value<GameObjectFilter>("type")
                    .withAnySubtype(it.value<Subtype>("first").value, it.value<Subtype>("second").value)
            }
            match { filter ->
                val (predicate, rest) = filter.stripTop<CardPredicate.Or>() ?: return@match null
                val subtypes = predicate.predicates.map {
                    (it as? CardPredicate.HasSubtype)?.subtype ?: return@match null
                }
                if (subtypes.size != 2) return@match null
                bind("first" to subtypes[0], "second" to subtypes[1], "type" to rest)
            }
        }

    /**
     * "non-Zombie creature" — the subtype layer's negation, which Oracle hyphenates where it writes
     * the colour negation as one word ("nonblack creature"). Two printed conventions for two
     * predicates, so they are two rules rather than one shape over a prefix.
     */
    private fun notSubtyped(inner: Phrase<GameObjectFilter>, name: String): Phrase<GameObjectFilter> =
        phrase("non-{subtype} {type}", name = name) {
            slot("subtype", Primitives.subtype)
            slot("type", inner)
            build { it.value<GameObjectFilter>("type").notSubtype(it.value<Subtype>("subtype")) }
            match { filter ->
                filter.stripTop<CardPredicate.NotSubtype>()
                    ?.let { (predicate, rest) -> bind("subtype" to predicate.subtype, "type" to rest) }
            }
        }

    /**
     * "Slivers", "a Goblin", "target Sliver" — the subtype standing alone.
     *
     * **It builds `Permanent`, not `Creature`, and that is the rules' reading rather than a
     * convenience.** A bare creature-type noun names every *permanent* with the subtype; the
     * adjectival "Sliver creature" is what narrows it to creatures. Zombie Master is the card that
     * proves the distinction is deliberate rather than stylistic: its first line says "Other Zombie
     * **creatures** have swampwalk" and its second says "Other **Zombies** have …", and the ability
     * that second line grants is spelled "Regenerate this **permanent**".
     *
     * It denotes what "Sliver permanent" denotes, so registering it as a canonical rule would leave
     * printing underdetermined between two real English spellings of one value. It is therefore an
     * [alternate]: cards printing the bare noun read correctly and print back as the adjective form,
     * which is a `VARIANT` — the reading was right and only the spelling moved.
     *
     * Unlike [subtyped] this leaf **is** ranked against the SDK's creature-type list, because here
     * the word alone has to imply a type. A guess about a word the SDK does not name would be the
     * reversible-but-wrong class: "target Scion" would read as a creature type nothing in Magic has.
     *
     * ### History, because the measurement is the interesting part
     *
     * This read `Creature` for a long time, and the differential is what closed it. Flipping the
     * line alone took the count from 2 divergences to **104** — 103 hand-written cards spelled the
     * bare noun as a creature filter, and for nearly all of them the two select the same permanents,
     * which is exactly why it survived review for so long. The flip was therefore reverted twice
     * before it landed *with* its card migration, in that order: the cards first, then this line,
     * with the differential as the check at every step. Flipping first would have left 103
     * unexplained divergences, which is the gate lying about which side is wrong.
     *
     * The migration also named three gaps in the SDK's own vocabulary, each now a facade beside its
     * creature-scoped twin: `DynamicAmounts.permanentsWithSubtype`,
     * `Conditions.ControlPermanentOfType`, and `TargetFilter.PermanentInYourGraveyard`. That a
     * bare-noun reading had no way to be *written* is the finding this module exists to produce.
     */
    private fun bareSubtype(plural: Boolean, name: String): Phrase<GameObjectFilter> =
        alternate(
            phrase<GameObjectFilter>("{subtype}", name = name) {
                slot("subtype", if (plural) Primitives.pluralCreatureSubtype else Primitives.creatureSubtype)
                build { GameObjectFilter.Permanent.withSubtype(it.value<Subtype>("subtype")) }
                canonical = false
            }
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
     * "nontoken Elf" — the token/nontoken layer.
     *
     * A prefix rather than a suffix, and a [CardPredicate] like the colour and keyword layers, so it
     * owns the top of the stack the same way they do. Oracle writes it as one word, which is why it
     * is a layer of its own and not a row in the type list: the noun it qualifies is still whatever
     * follows, subtype and all.
     */
    private fun nontoken(inner: Phrase<GameObjectFilter>, name: String): Phrase<GameObjectFilter> =
        phrase("nontoken {type}", name = name) {
            slot("type", inner)
            build { it.value<GameObjectFilter>("type").nontoken() }
            match { filter ->
                filter.stripTop<CardPredicate.IsNontoken>()?.let { (_, rest) -> bind("type" to rest) }
            }
        }

    /** "creature with mana value 3 or less" — Sunstrike Legionnaire's tap target. */
    private fun withManaValueAtMost(inner: Phrase<GameObjectFilter>, name: String): Phrase<GameObjectFilter> =
        phrase("{type} with mana value {n} or less", name = name) {
            slot("type", inner)
            slot("n", Primitives.cardinal)
            build { it.value<GameObjectFilter>("type").manaValueAtMost(it.int("n")) }
            match { filter ->
                filter.stripTop<CardPredicate.ManaValueAtMost>()
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
        val named = typeNoun(plural)
        val types = oneOf(
            "a permanent type or subtype$suffix",
            named,
            subtyped(named, "a permanent of a subtype$suffix"),
            notSubtyped(named, "a permanent of another subtype$suffix"),
            anySubtype(named, "a permanent of either subtype$suffix"),
            bareSubtype(plural, "a subtype standing alone$suffix"),
        )
        val counted = oneOf(
            "a permanent or token$suffix",
            types,
            nontoken(types, "a nontoken permanent$suffix"),
        )
        val coloured = oneOf(
            "a coloured permanent$suffix",
            counted,
            colour(counted, "a coloured permanent$suffix"),
            notColour(counted, "a permanent of another colour$suffix"),
            anyColour(counted, "a permanent of either colour$suffix"),
        )
        val qualified = oneOf(
            "a qualified permanent$suffix",
            coloured,
            withKeyword(coloured, "a permanent with a keyword$suffix"),
            withoutKeyword(coloured, "a permanent without a keyword$suffix"),
            withPowerAtLeast(coloured, "a permanent with power at least$suffix"),
            withPowerAtMost(coloured, "a permanent with power at most$suffix"),
            withManaValueAtMost(coloured, "a permanent with mana value at most$suffix"),
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

    /**
     * "Sliver" as a bare quality of a *card* — the noun in "Sliver spells can't be countered."
     *
     * Not a member of the cascade, and not [bareSubtype] either: this one carries **no card type at
     * all**, because a spell on the stack is not a permanent and the sentence names the subtype
     * alone. `GameObjectFilter.Any.withSubtype(…)` is what the hand-written cards use for it, which
     * is the difference from the battlefield nouns above that all imply "creature".
     */
    val subtypeOnly: Phrase<GameObjectFilter> = phrase("{subtype}", name = "a subtype") {
        slot("subtype", Primitives.subtype)
        build { GameObjectFilter.Any.withSubtype(it.value<Subtype>("subtype")) }
        match { filter ->
            val subtype = (filter.cardPredicates.singleOrNull() as? CardPredicate.HasSubtype)?.subtype
                ?: return@match null
            if (filter != GameObjectFilter.Any.withSubtype(subtype)) return@match null
            bind("subtype" to subtype)
        }
    }

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
