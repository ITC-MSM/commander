package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.constant
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.predicates.CardPredicate

/**
 * The noun phrase a *spell* is described by — "a creature spell", "a noncreature spell", "an instant
 * or sorcery spell", "a blue spell", "a Spirit or Arcane spell", "a spell with mana value 4 or
 * greater".
 *
 * It denotes the same type as [Filters], `mtg-sdk`'s [GameObjectFilter], and is written the same way
 * — layers, never composition, each owning exactly one field and stripping precisely it. What makes
 * it a family of its own rather than rows in [Filters] is the **head noun**. A permanent phrase's
 * head is the card type ("creature", "nonbasic land"); a spell phrase's head is the literal word
 * "spell", and the card type stands in front of it as an adjective. So "creature" names
 * `GameObjectFilter.Creature` in both files and prints as two different strings, which is one printed
 * form per model in two disjoint positions rather than two forms for one — the positions being a
 * battlefield noun and a stack noun, and nothing reaching both.
 *
 * That is also why the layers here are a *subset* of [Filters]'. A spell has no controller clause
 * ("a creature spell you control" is not printed — the caster is the trigger's subject, not the
 * noun's), is never tapped, attacking or a token, and has no power to compare; every layer that owns
 * one of those fields would be a rule with nothing in the corpus to read. What is left is the three
 * axes a card carries on the stack: its types, its colour, and its mana value.
 *
 * ### The layers, innermost first
 *
 * | Layer | Owns | Surface |
 * |---|---|---|
 * | [head] | the whole predicate set of a named card-type quality | "spell", "creature spell" |
 * | [subtyped] / [anySubtype] | the last [CardPredicate] when it is a subtype one | "Spirit spell" |
 * | [colour] | the last [CardPredicate] when it is a colour one | "blue spell" |
 * | [manaValueAtLeast] | the last [CardPredicate] when it is a mana-value one | "…with mana value 4 or greater" |
 *
 * The nesting is the order English uses and the order the fluent builders append in, exactly as
 * [Filters] states: "an Eldrazi creature spell with mana value 7 or greater" builds
 * `Creature.withSubtype(Eldrazi).manaValueAtLeast(7)`, and each layer strips its own top.
 *
 * ### The subtype join is "or" here and "and/or" in [Filters], and that is not two forms for one model
 *
 * [Filters.anySubtype] spells "Bird and/or Cleric permanent"; the corpus spells the same predicate
 * shape on a spell as "a **Spirit or Arcane** spell". Both build a [CardPredicate.Or] of two
 * `HasSubtype`s — but [Filters.anySubtype]'s inner is a *type noun*, so the value it produces always
 * carries a card-type predicate under the `Or`, and the value this rule produces never does. The two
 * are therefore different values and each has exactly one printed form. Deriving the join from the
 * head noun would be the alternative, and it would be a rule that reads a templating habit rather
 * than a model.
 *
 * ### Why "colorless" is absent
 *
 * `GameObjectFilter` publishes `Multicolored` and the whole colour vocabulary, but no colourless
 * constant or builder — [CardPredicate.IsColorless] exists with nothing curated in front of it. This
 * module builds through the SDK's facades and does not add to them, so the ten cards printing "a
 * colorless spell" decline and the gap is named here rather than routed around with a raw `copy`.
 *
 * Compare [Stack], which enumerates a spell noun of its own: there the value is a `TargetFilter`
 * carrying `Zone.STACK`, because a *target* on the stack is a requirement rather than an event's
 * filter. Same English, different SDK slot, and neither position can reach the other's rules.
 */
object Spells {

    /**
     * The card-type qualities Oracle writes in front of "spell", each a curated `GameObjectFilter`.
     *
     * Enumerated for [Filters]' reason and not a weaker one: "instant or sorcery" is an ordered
     * [CardPredicate.Or] and "artifact creature" would be two predicates, and English distinguishes
     * them only by the words. "historic" is the clearest case — it is a defined term in the
     * Comprehensive Rules' glossary meaning *artifact, legendary or Saga*, and no shape over the
     * three predicates could recover the one word that names them.
     */
    private val QUALITIES: List<Pair<String, GameObjectFilter>> = listOf(
        "creature" to GameObjectFilter.Creature,
        "noncreature" to GameObjectFilter.Noncreature,
        "artifact" to GameObjectFilter.Artifact,
        "enchantment" to GameObjectFilter.Enchantment,
        "instant" to GameObjectFilter.Instant,
        "sorcery" to GameObjectFilter.Sorcery,
        "planeswalker" to GameObjectFilter.Planeswalker,
        "permanent" to GameObjectFilter.Permanent,
        "instant or sorcery" to GameObjectFilter.InstantOrSorcery,
        "multicolored" to GameObjectFilter.Multicolored,
        "historic" to GameObjectFilter.Historic,
        "legendary" to GameObjectFilter.Any.legendary(),
    )

    /**
     * "spell", "creature spell" — the head noun, with the card-type quality that may precede it.
     *
     * The unqualified row builds [GameObjectFilter.Any], which is the value every cast-trigger facade
     * defaults to, so "a spell" and "a creature spell" are one alternation rather than an optional
     * literal that could print either way.
     */
    private val head: Phrase<GameObjectFilter> = oneOf(
        "a spell",
        listOf(constant("spell", GameObjectFilter.Any)) +
            QUALITIES.map { (surface, filter) -> constant("$surface spell", filter) },
    )

    /** Strip the top of the predicate stack when it is the kind this layer owns. */
    private inline fun <reified P : CardPredicate> GameObjectFilter.stripTop(): Pair<P, GameObjectFilter>? {
        val top = cardPredicates.lastOrNull() as? P ?: return null
        return top to copy(cardPredicates = cardPredicates.dropLast(1))
    }

    /**
     * The subtype leaf this file reads, which is [Primitives.subtype] minus the words the SDK
     * models as something other than a characteristic.
     *
     * There is exactly one today and it is not an exception to a rule, it *is* the rule: an
     * "Adventure spell" is a spell **cast as** an Adventure (CR 715.3), which
     * `SpellCastPredicate.CastAsAdventure` says in its own KDoc — "this is about how the card was
     * cast, not what the card is", and an adventurer card cast as its creature half does not
     * satisfy it. `Any.withSubtype(Adventure)` is a different claim, about the object on the stack,
     * and the two select different spells. The differential caught it on Storyteller Pixie: the
     * reading round-tripped byte-perfectly and meant a card the engine would never fire.
     *
     * So the word is spelled by [Triggers.castRules]' own row instead, and refusing it here is what
     * keeps that one printed form from having two models — which is ambiguity by construction
     * rather than a preference. Every other capitalized word in this position — Spirit, Arcane,
     * Lesson, Omen, Aura, Equipment, and the fifty-odd creature types — is a characteristic of the
     * card, so the leaf stays otherwise ungated for [Filters.subtyped]'s reason: the head noun
     * supplies the card type and nothing is being guessed.
     *
     * Declared above the rule that reads it, per this module's ordering rule: object initializers
     * run in declaration order.
     */
    private val MODELLED_ELSEWHERE: Set<Subtype> = setOf(Subtype("Adventure"))

    /** [MODELLED_ELSEWHERE]'s gate applied to [Primitives.subtype]. */
    private val spellSubtype: Phrase<Subtype> = phrase("{word}", name = "a spell's subtype") {
        slot("word", Primitives.subtype)
        build { it.value<Subtype>("word").takeIf { s -> s !in MODELLED_ELSEWHERE } }
        match { subtype -> if (subtype in MODELLED_ELSEWHERE) null else bind("word" to subtype) }
    }

    /** "a Merfolk spell", "an Eldrazi creature spell" — one subtype in front of the head. */
    private fun subtyped(inner: Phrase<GameObjectFilter>): Phrase<GameObjectFilter> =
        phrase("{subtype} {spell}", name = "a spell of a subtype") {
            slot("subtype", spellSubtype)
            slot("spell", inner)
            build { it.value<GameObjectFilter>("spell").withSubtype(it.value<Subtype>("subtype")) }
            match { filter ->
                filter.stripTop<CardPredicate.HasSubtype>()
                    ?.let { (predicate, rest) -> bind("subtype" to predicate.subtype, "spell" to rest) }
            }
        }

    /** "a Spirit or Arcane spell" — two subtypes, either of which qualifies. */
    private fun anySubtype(inner: Phrase<GameObjectFilter>): Phrase<GameObjectFilter> =
        phrase("{first} or {second} {spell}", name = "a spell of either subtype") {
            slot("first", spellSubtype)
            slot("second", spellSubtype)
            slot("spell", inner)
            build {
                it.value<GameObjectFilter>("spell")
                    .withAnySubtype(it.value<Subtype>("first").value, it.value<Subtype>("second").value)
            }
            match { filter ->
                val (predicate, rest) = filter.stripTop<CardPredicate.Or>() ?: return@match null
                val subtypes = predicate.predicates.map {
                    (it as? CardPredicate.HasSubtype)?.subtype ?: return@match null
                }
                if (subtypes.size != 2) return@match null
                bind("first" to subtypes[0], "second" to subtypes[1], "spell" to rest)
            }
        }

    /** "a blue spell", "a red creature spell". */
    private fun colour(inner: Phrase<GameObjectFilter>): Phrase<GameObjectFilter> =
        phrase("{color} {spell}", name = "a coloured spell") {
            slot("color", Primitives.color)
            slot("spell", inner)
            build { it.value<GameObjectFilter>("spell").withColor(it.value("color")) }
            match { filter ->
                filter.stripTop<CardPredicate.HasColor>()
                    ?.let { (predicate, rest) -> bind("color" to predicate.color, "spell" to rest) }
            }
        }

    /**
     * "a spell with mana value 4 or greater" — a suffix in English and the top of the stack in the
     * model, so it is the outermost layer.
     *
     * The number is [Primitives.cardinal] rather than [Cardinals.word] because Oracle writes this one
     * in digits: "with mana value 4 or greater", never "with mana value four or greater".
     */
    private fun manaValueAtLeast(inner: Phrase<GameObjectFilter>): Phrase<GameObjectFilter> =
        phrase("{spell} with mana value {n} or greater", name = "a spell with mana value at least") {
            slot("spell", inner)
            slot("n", Primitives.cardinal)
            build { it.value<GameObjectFilter>("spell").manaValueAtLeast(it.int("n")) }
            match { filter ->
                filter.stripTop<CardPredicate.ManaValueAtLeast>()
                    ?.let { (predicate, rest) -> bind("spell" to rest, "n" to predicate.min) }
            }
        }

    /**
     * The whole cascade — "spell", "noncreature spell", "blue Spirit spell with mana value 3 or
     * greater".
     *
     * Each level carries the level below as its first alternative, so a filter using none of a
     * layer's vocabulary is printed by the layer that owns what it does use.
     */
    val spell: Phrase<GameObjectFilter> = run {
        val subtypes = oneOf("a spell of a card type or subtype", head, subtyped(head), anySubtype(head))
        val coloured = oneOf("a coloured spell", subtypes, colour(subtypes))
        oneOf("a spell noun phrase", coloured, manaValueAtLeast(coloured))
    }

    /**
     * "a spell", "an instant or sorcery spell" — the same phrase with its indefinite article.
     *
     * The article is derived from [spell]'s own printed form in both directions, exactly as
     * [Filters.indefinite] derives it: English picks it from the sound of the next word, which is a
     * property of the spelling rather than of the model, so a rule whose article disagrees refuses in
     * both directions and printing stays determined.
     */
    val indefinite: Phrase<GameObjectFilter> = oneOf(
        "a spell with its article",
        article("a"),
        article("an"),
    )

    private fun article(article: String): Phrase<GameObjectFilter> =
        phrase("$article {spell}", name = "\"$article\" plus a spell") {
            slot("spell", spell)
            build { it.value<GameObjectFilter>("spell").takeIf { f -> articleFor(f) == article } }
            match { f -> if (articleFor(f) == article) bind("spell" to f) else null }
        }

    /** The article [spell] would print [f] with, or null when it cannot print it at all. */
    private fun articleFor(f: GameObjectFilter): String? {
        val head = spell.unparse(f)?.firstOrNull()?.lowercaseChar() ?: return null
        return if (head in listOf('a', 'e', 'i', 'o', 'u')) "an" else "a"
    }
}
