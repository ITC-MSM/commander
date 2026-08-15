package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.normalize.Normalizer
import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.alternate
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.constant
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.assay.syntax.separated
import com.wingedsheep.assay.syntax.token
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * The leaf rules every other rule is built from. Slots are themselves phrases, recursively, so
 * these are ordinary bidirectional rules with nothing special about them beyond being terminals.
 *
 * Everything lives inside the object and is declared before it is used: object initializers run in
 * declaration order, and a rule that referenced a later one would read a null out of a
 * half-initialized object rather than fail loudly.
 */
object Primitives {

    /**
     * A whole number written in digits — "Annihilator **2**".
     *
     * The pattern refuses a leading zero on purpose: `007` would read as 7 and print back as "7",
     * a round-trip failure attributable to the leaf rather than to whatever rule used it. Refusing
     * to *read* it turns the same input into a clean decline instead.
     */
    val cardinal: Phrase<Int> = token(
        name = "a number",
        pattern = Regex("""0|[1-9][0-9]*"""),
        read = { it.toInt() },
        write = { it.toString() },
    )

    /**
     * A power/toughness modifier pair — "+3/+3", "-2/-0", "+0/+2".
     *
     * ### Why this is one leaf and not two signed integers
     *
     * A zero modifier is printed with a sign the model cannot hold: `Fixed(0)` is `Fixed(0)`, but the
     * text says "-2/-0" on one card and "+0/+2" on another. Two independent leaves could never
     * choose, because neither can see the other component. One leaf can, and the corpus states the
     * rule unambiguously — across all 38,626 Oracle texts a zero **always takes the sign of the other
     * component**, and "+0/+0" is the only spelling when both are zero. There is no `+3/-0` or
     * `-0/+2` in Magic. The touchstone holds this to a corpus-wide zero mismatches, which is the
     * check that the rule is Wizards' and not this file's.
     *
     * The value is a bare [Pair] rather than a type of its own: both components go straight into
     * `Effects.ModifyStats`, so nothing here is an intermediate representation of "what the text
     * means" — it is the two numbers the printed pair carries, in the order it carries them.
     */
    val statModifiers: Phrase<Pair<Int, Int>> = token(
        name = "a power/toughness modifier",
        pattern = Regex("""[+-](?:0|[1-9][0-9]*)/[+-](?:0|[1-9][0-9]*)"""),
        read = { text ->
            val (power, toughness) = text.split("/")
            power.toInt() to toughness.toInt()
        },
        write = { (power, toughness) -> "${signed(power, toughness)}/${signed(toughness, power)}" },
    )

    /** [value]'s printed form, taking [sibling]'s sign when it is zero and has none of its own. */
    private fun signed(value: Int, sibling: Int): String = when {
        value != 0 -> if (value > 0) "+$value" else "$value"
        sibling < 0 -> "-0"
        else -> "+0"
    }

    /**
     * A run of mana symbols — `{2}{U}`, `{W/P}`, `{X}`. Symbols are lexed as tokens and never as
     * prose (the design's symbol rule); a symbol the SDK's [ManaCost] cannot express — `{S}`, for
     * one — makes this leaf decline rather than throw, so the card is counted, not lost.
     */
    val manaCost: Phrase<ManaCost> = token(
        name = "a mana cost",
        pattern = Regex("""(?:\{[^{}]+})+"""),
        read = { ManaCost.parse(it) },
        write = { it.toString() },
    )

    val color: Phrase<Color> = oneOf(
        "a color",
        Color.entries.map { constant(it.displayName.lowercase(), it) },
    )

    /**
     * The subject of a sentence when that subject is the card itself — "**~** deals 2 damage…",
     * "**it** deals 2 damage…".
     *
     * Oracle uses the pronoun once a clause earlier in the same ability has already named the
     * source: Fire Imp prints "When this creature enters, **it** deals 2 damage to target creature."
     * Both spellings denote the same thing and the model has no room for which one was printed, so
     * the name is canonical and the pronoun is an [com.wingedsheep.assay.syntax.alternate]. That is
     * the same treatment [com.wingedsheep.assay.normalize.Normalizer] gives "this creature" versus
     * the card's own name, one level further in: normalization abstracts both to `~`, and this rule
     * abstracts the pronoun that stands for `~`.
     *
     * The value is [Unit] because a subject that is always the source carries no information; what
     * the rule buys is that every sentence in the grammar spelling `~` reads the pronoun too,
     * without a second copy of the rule.
     */
    val self: Phrase<Unit> = oneOf(
        "this permanent",
        constant(Normalizer.SELF, Unit),
        alternate(constant("it", Unit)),
    )

    /**
     * Plurals whose singular the general rules would get wrong *in either direction*.
     *
     * The "-ves" family needs to be listed rather than derived, because the inverse is not a rule:
     * `Werewolf` pluralizes to "Werewolves" but `Lhurgoyf` pluralizes to "Lhurgoyfs", and nothing in
     * the spelling says which. [singularCandidates] carries a general "-ves" reading as a *fallback*
     * for reading unlisted types; printing only ever uses this map.
     */
    private val IRREGULAR_PLURALS = mapOf(
        "Elves" to "Elf",
        "Dwarves" to "Dwarf",
        "Wolves" to "Wolf",
        "Werewolves" to "Werewolf",
        "Thieves" to "Thief",
        "Scarecrows" to "Scarecrow",
    )

    private val SUBTYPE_PLURAL = Regex("""[A-Z][A-Za-z-]*s""")

    /**
     * The types the SDK names — creature types per the Comprehensive Rules' creature-type list, plus
     * the basic land types, which appear in the same slot ("Affinity for **Plains**").
     *
     * Used to **rank** candidate readings, not to gate them: a candidate that names a real type wins
     * over one that does not, and where no candidate is known the ordinary "-s" reading still
     * applies. That distinction is deliberate, because this set is not the whole truth — the SDK
     * exposes a list for creature and basic land types only, so artifact, enchantment and
     * nonbasic-land types ("Affinity for Equipment", "for Food", "for Gates") are real subtypes that
     * are simply absent from it. Gating on the set would decline them, trading one wrong answer for
     * a worse one.
     *
     * Declining unknown types outright is the better end state and is what "declining is success"
     * argues for; it needs the SDK to publish the remaining subtype lists first.
     * `Subtype.fromName` is not that publication — it title-cases anything for
     * forward-compatibility, so it answers "yes" to everything and cannot rank.
     *
     * Declared above [pluralSubtype] on purpose: object initializers run in declaration order, and
     * the rule below reads [SUBTYPE_PLURAL] while *it* is initializing.
     */
    private val KNOWN_SUBTYPES: Set<String> =
        (Subtype.ALL_CREATURE_TYPES + Subtype.ALL_BASIC_LAND_TYPES).toSet()

    /**
     * A creature type written in its plural surface form — "protection from **Goblins**".
     *
     * The plural lives in the leaf rather than in a template literal because a `{subtype}` slot
     * followed by a literal `"s"` would let the slot swallow the "s" and strand the literal.
     *
     * **De-pluralizing is checked against the SDK's own type list, never guessed.** Stripping the
     * "s" is only a *candidate*; the reading is accepted when the result is a subtype the SDK
     * actually names, and declines otherwise. That is the fix for the reversible-but-wrong class:
     * "Elves" naively yields `Elve` and "Plains" yields `Plain`, and both round-trip perfectly while
     * meaning nothing. The differential gate caught the second one on its first run — `Plain` is not
     * a type, `Subtype.PLAINS` is `Plains`, and only the SDK's list knows that.
     *
     * Candidates are tried in [singularCandidates]' order, so an English-plural reading beats an
     * invariant one where both name a real type.
     */
    val pluralSubtype: Phrase<Subtype> = token(
        name = "a creature type",
        pattern = SUBTYPE_PLURAL,
        read = ::readPluralSubtype,
        write = ::writePluralSubtype,
    )

    /**
     * "white" — **one** colour.
     *
     * Multi-quality protection is not a scope, it is several abilities: CR 702.16g makes
     * "protection from [A] and from [B]" shorthand for two protection abilities, and CR 702.11f says
     * the same for hexproof. The join therefore lives one level up, in [scopeRun], and this leaf
     * stays a single quality. [ProtectionScope.Colors] is consequently a scope the grammar never
     * produces — an SDK spelling of something the rules define as two abilities, reported rather
     * than emitted.
     */
    private val colorScope: Phrase<ProtectionScope> = phrase("{color}", name = "a colour") {
        slot("color", color)
        build { ProtectionScope.Color(it.value("color")) }
        match { (it as? ProtectionScope.Color)?.let { c -> bind("color" to c.color) } }
    }

    private val subtypeScope: Phrase<ProtectionScope> = phrase("{subtype}", name = "a creature type") {
        slot("subtype", pluralSubtype)
        build { ProtectionScope.Subtype(it.value<Subtype>("subtype").value) }
        match { (it as? ProtectionScope.Subtype)?.let { s -> bind("subtype" to Subtype(s.subtype)) } }
    }

    /**
     * What a protection or hexproof ability is protected *from* — the quality named by the
     * protection keyword ability in the Comprehensive Rules.
     *
     * Note the deliberate omission: no rule spells "each opponent" as
     * `Simple(PROTECTION_FROM_EACH_OPPONENT)`, even though the SDK has that enum constant. Two
     * rules for one text would be genuine ambiguity — two different models for one reading — and
     * the design says never to pick one silently. That the enum constant and
     * [ProtectionScope.EachOpponent] are two spellings of one thing is an SDK finding, reported
     * rather than papered over.
     *
     * The card-type list is the set that actually appears in printed Oracle text, not everything
     * [ProtectionScope.CardType] could hold. A rule for a phrasing no card uses is a rule nothing
     * ever checks, and adding one later is a single line.
     */
    val protectionScope: Phrase<ProtectionScope> = oneOf(
        "a protection quality",
        colorScope,
        constant("everything", ProtectionScope.Everything),
        constant("each opponent", ProtectionScope.EachOpponent),
        subtypeScope,
        constant("artifacts", ProtectionScope.CardType("Artifact")),
        constant("creatures", ProtectionScope.CardType("Creature")),
        constant("enchantments", ProtectionScope.CardType("Enchantment")),
        constant("instants", ProtectionScope.CardType("Instant")),
        constant("planeswalkers", ProtectionScope.CardType("Planeswalker")),
        constant("lands", ProtectionScope.CardType("Land")),
    )

    /** "black and from red" — the two-quality join, which is every such card but one. */
    private val scopePair: Phrase<List<ProtectionScope>> =
        phrase("{first} and from {second}", name = "two qualities") {
            slot("first", protectionScope)
            slot("second", protectionScope)
            build { listOf(it.value("first"), it.value("second")) }
            match { scopes ->
                scopes.takeIf { it.size == 2 }?.let { bind("first" to it[0], "second" to it[1]) }
            }
        }

    /**
     * "Vampires, from Werewolves, and from Zombies" — three or more, with the Oxford comma the
     * printed cards use. One card in the corpus, and it is written rather than declined because the
     * shape is the same rule and the alternative is a decline that reads as a missing capability.
     */
    private val scopeSeries: Phrase<List<ProtectionScope>> =
        phrase("{most}, and from {last}", name = "three or more qualities") {
            slot("most", separated("qualities", protectionScope, ", from ", min = 2))
            slot("last", protectionScope)
            build { it.value<List<ProtectionScope>>("most") + it.value<ProtectionScope>("last") }
            match { scopes ->
                scopes.takeIf { it.size >= 3 }?.let { bind("most" to it.dropLast(1), "last" to it.last()) }
            }
        }

    /**
     * Two or more qualities, joined the way printed Oracle text joins them.
     *
     * The two shapes cannot both match one text — [scopeSeries] needs at least three qualities and
     * [scopePair] takes exactly two — so the run has one reading in each direction, and printing
     * picks the shape from the count rather than from a preference.
     */
    val scopeRun: Phrase<List<ProtectionScope>> = oneOf("two or more qualities", scopePair, scopeSeries)

    /**
     * Singular readings of a printed plural, best first: the ordinary "-s" plural, then an invariant
     * one ("Plains"), then "-ies" ("Allies" → `Ally`) and "-ves" ("Werewolves" → `Werewolf`).
     * Ordinary-first is what keeps "Zombies" reading as `Zombie` rather than as `Zomby`.
     *
     * The list is ranked in [readPluralSubtype] rather than taken in order — the first candidate
     * that names a type the SDK knows wins, and only if none does is the ordinary reading used.
     */
    private fun singularCandidates(plural: String): List<String> = listOfNotNull(
        IRREGULAR_PLURALS[plural],
        plural.dropLast(1),
        plural,
        (plural.dropLast(3) + "y").takeIf { plural.endsWith("ies") },
        (plural.dropLast(3) + "f").takeIf { plural.endsWith("ves") },
    )

    /**
     * The inverse, same discipline: candidate spellings, and the caller keeps the first that reads
     * back to the value it started from. Deriving the printed form from [readPluralSubtype] rather
     * than restating the rules is what stops the two halves drifting — the failure mode the kernel's
     * [com.wingedsheep.assay.syntax.token] check exists to catch, avoided here by construction.
     */
    private fun pluralCandidates(singular: String): List<String> = listOfNotNull(
        IRREGULAR_PLURALS.entries.firstOrNull { it.value == singular }?.key,
        // An invariant plural is its own plural, and must be offered before "…s" would win.
        singular.takeIf { it.endsWith("s") },
        // Consonant + y pluralizes as "-ies" ("Ally" → "Allies"); vowel + y does not ("Monkeys").
        (singular.dropLast(1) + "ies").takeIf { singular.endsWithConsonantY() },
        "${singular}s",
    )

    private fun String.endsWithConsonantY(): Boolean =
        length >= 2 && endsWith("y") && !isVowel(this[length - 2])

    private fun isVowel(c: Char) = c.lowercaseChar() in "aeiou"

    /**
     * A known type beats an unknown one; failing that, the ordinary "-s" reading stands.
     *
     * The ranking is the whole fix for the reversible-but-wrong class the differential surfaced:
     * "Plains" offers `Plain` (nothing) and `Plains` (a real basic land type), and without the
     * ranking the first one wins and round-trips forever.
     */
    private fun readPluralSubtype(plural: String): Subtype? {
        val candidates = singularCandidates(plural)
        val known = candidates.firstOrNull { it in KNOWN_SUBTYPES }
        return (known ?: candidates.firstOrNull()?.takeIf { it.isNotEmpty() })?.let(::Subtype)
    }

    private fun writePluralSubtype(subtype: Subtype): String? =
        pluralCandidates(subtype.value).firstOrNull { candidate ->
            SUBTYPE_PLURAL.matchEntire(candidate) != null && readPluralSubtype(candidate) == subtype
        }
}
