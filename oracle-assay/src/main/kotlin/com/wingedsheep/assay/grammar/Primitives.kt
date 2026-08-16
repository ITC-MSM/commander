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
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.scripting.ProtectionScope
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

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
     * A **kind** of counter, as the noun before the word "counter" — "+1/+1", "stun", "first strike".
     *
     * ### Gated on the SDK's own list, for [creatureSubtype]'s reason
     *
     * The model field is a bare `String`, so an ungated leaf would read *any* lowercase word as a
     * counter kind and round-trip it perfectly — "put a growing counter on it" naming a counter Magic
     * does not have, byte-exact in both directions. [CounterType.fromName] is the SDK's own answer to
     * "is this a counter", the same function `StatePredicate.HasCounter` parses with, so a word it
     * rejects makes this leaf decline rather than invent a kind. That is the difference between
     * recovering information and inventing it, and it is why "Elves" → `Elve` could happen here too.
     *
     * ### The second word, and why it needs a lookahead
     *
     * "first strike" and "double strike" are two-word kinds, and a leaf reads exactly one regex match
     * — [com.wingedsheep.assay.syntax.token] does not retry a shorter one when the gate rejects. So a
     * greedy second word would swallow the template's own "counter" on every single-word kind and
     * decline the lot. The lookahead spells out what the noun cannot be, which costs one clause and
     * keeps both quantities of every kind readable.
     */
    val counterKind: Phrase<String> = token(
        name = "a counter kind",
        pattern = Regex("""[+-][0-9]+/[+-][0-9]+|[a-z]+(?: (?!counters?\b)[a-z]+)?"""),
        read = { it.takeIf(::isCounterKind) },
        write = { it.takeIf(::isCounterKind) },
    )

    private fun isCounterKind(name: String) = CounterType.fromName(name) != null

    /**
     * One counter of a kind, **article included** — "a +1/+1", "an aim", "an hourglass".
     *
     * ### Why the article is inside the leaf rather than a literal in the template
     *
     * English picks "a" or "an" from the sound of the following word, which no separate slot can see.
     * Two rules — one spelling "a", one spelling "an" — would leave printing undetermined by the
     * model, and that is invariant 2 rather than a style preference: `oneOf` would pick the first
     * that could express the value and the other would be unreachable. One leaf can see both halves,
     * which is exactly [statModifiers]' argument about the sign of a zero.
     *
     * ### The rule is Wizards', and the corpus states it without an exception
     *
     * Across all 34,882 Oracle texts **no counter kind is ever spelled both ways**: 223 kinds take
     * "a", 38 take "an", and the two sets are disjoint. So the article is a total function of the
     * kind and this leaf can be its inverse. The letter rule predicts all but three of them —
     * "an hour", "an hourglass" (silent h) and "a unity" (the /juː/ onset) — and only `hourglass` is
     * a kind the SDK names, so [SILENT_H] is one entry rather than a list that will grow. A kind
     * whose article this got wrong could not round-trip: `token` re-reads what it writes on every
     * call, and [read] rejects an article that disagrees with [article].
     */
    val singularCounterKind: Phrase<String> = token(
        name = "a counter kind",
        pattern = Regex("""an? (?:[+-][0-9]+/[+-][0-9]+|[a-z]+(?: (?!counters?\b)[a-z]+)?)"""),
        read = { text ->
            val kind = text.substringAfter(' ')
            kind.takeIf { isCounterKind(it) && text.substringBefore(' ') == article(it) }
        },
        write = { kind -> kind.takeIf(::isCounterKind)?.let { "${article(it)} $it" } },
    )

    /** Silent-h kinds, which take "an" against the letter rule. Only `hourglass` is an SDK counter. */
    private val SILENT_H = setOf("hour", "hourglass")

    private fun article(kind: String): String =
        if (kind in SILENT_H || kind.first() in "aeiou") "an" else "a"

    /**
     * The same kind as [CounterTypeFilter], which is how `EntersWithCounters` spells it.
     *
     * ### One concept, two SDK types — and a `Named` spelling the grammar must never emit
     *
     * An effect says which counter it means with a `String`; a replacement effect says it with a
     * [CounterTypeFilter], whose `Named` case takes that same string. So `PlusOnePlusOne` and
     * `Named("+1/+1")` are two spellings of one value, and registering both would be genuine
     * ambiguity with nothing for the printer to choose between. This maps to the dedicated case
     * wherever the SDK has published one and to `Named` only where it has not — and [counterKindOf],
     * its inverse, **refuses** a `Named` carrying a name that has a dedicated case, so a card written
     * the minority way reports as a divergence rather than quietly agreeing.
     */
    fun counterFilter(kind: String): CounterTypeFilter =
        DEDICATED_COUNTER_FILTERS[kind] ?: CounterTypeFilter.Named(kind)

    /** [counterFilter]'s inverse; null where the value is a spelling this grammar does not emit. */
    fun counterKindOf(filter: CounterTypeFilter): String? = when (filter) {
        is CounterTypeFilter.Named -> filter.name.takeIf { it !in DEDICATED_COUNTER_FILTERS }
        else -> DEDICATED_COUNTER_FILTERS.entries.firstOrNull { it.value == filter }?.key
    }

    private val DEDICATED_COUNTER_FILTERS: Map<String, CounterTypeFilter> = mapOf(
        Counters.PLUS_ONE_PLUS_ONE to CounterTypeFilter.PlusOnePlusOne,
        Counters.MINUS_ONE_MINUS_ONE to CounterTypeFilter.MinusOneMinusOne,
        Counters.PLUS_ONE_PLUS_ZERO to CounterTypeFilter.PlusOnePlusZero,
        Counters.PLUS_ZERO_PLUS_ONE to CounterTypeFilter.PlusZeroPlusOne,
        Counters.MINUS_ONE_MINUS_ZERO to CounterTypeFilter.MinusOneMinusZero,
        Counters.MINUS_ZERO_MINUS_ONE to CounterTypeFilter.MinusZeroMinusOne,
        Counters.LOYALTY to CounterTypeFilter.Loyalty,
    )

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

    /**
     * The generic amount a [manaCost] denotes, or null where it says anything a plain number cannot
     * hold — `{W}`, `{1}{U}`, `{X}`.
     *
     * The generic/coloured split is a *model* distinction the SDK makes twice over (`ReduceGeneric`
     * against `ReduceColored`, `AttackTax` against nothing), and both call sites need the same
     * answer, so it is one function rather than a private copy in each. The test is a round trip
     * rather than a field read: a cost equals its own generic amount respelled exactly when it holds
     * nothing else.
     */
    fun genericAmount(cost: ManaCost): Int? =
        cost.takeIf { it == ManaCost.parse("{${it.genericAmount}}") }?.genericAmount

    /**
     * A card's **name**, as a card that searches for one by name prints it — "a card named Scion of
     * Darkness".
     *
     * The pattern is what makes this readable at all: a name has spaces in it, so a naive run of
     * word characters would swallow the rest of the sentence. Magic's names are capitalized words
     * joined by a small closed set of lowercase particles, and only a *capitalized* word may start
     * or continue the run — so "Scion of Darkness and put it onto the battlefield" stops after
     * "Darkness", because "and" is not one of the particles and "put" is not capitalized.
     *
     * A name whose spelling falls outside that shape declines, which is the honest answer: the
     * alternative is a leaf that guesses where a name ends, and a wrong guess would round-trip while
     * naming a different card.
     */
    val cardName: Phrase<String> = token(
        name = "a card name",
        pattern = Regex("""[A-Z][A-Za-z'\-]*(?: (?:of|the|to|and(?= [A-Z])|[A-Z][A-Za-z'\-]*))*"""),
        read = { it },
        write = { it },
    )

    val color: Phrase<Color> = oneOf(
        "a color",
        Color.entries.map { constant(it.displayName.lowercase(), it) },
    )

    /**
     * The same colour written as its **mana symbol** — "{R}", "{G}" — which is how a mana-producing
     * clause names it ("in any combination of {R} and/or {G}").
     *
     * A separate rule rather than a spelling of [color], because the two are never
     * interchangeable: a filter says "red creature" and never "{R} creature", and a mana clause says
     * "{R}" and never "red". Registering them as alternates of one rule would let each print in the
     * other's sentence.
     */
    val manaSymbolColor: Phrase<Color> = oneOf(
        "a mana symbol",
        Color.entries.map { constant("{${it.symbol}}", it) },
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
     * [self]'s two halves, for the one context that has to tell them apart.
     *
     * Inside a *filtered* trigger the two spellings stop denoting the same thing: "Whenever a Rat you
     * control becomes blocked, **it** gets +2/+0" pumps the Rat, while "Whenever a Rat you control
     * becomes blocked, **~** gets +2/+0" pumps the source. English resolves the anaphor to the most
     * recently mentioned object, and in a filtered trigger that is the filter's match rather than the
     * card — which is why this is a *third* anaphor position beside [self] and [Continuations], and
     * why the two surfaces have to be separable rather than one rule with an alternate.
     *
     * Everywhere else they still denote one thing and [self] is what rules take. See
     * [SelfSteps.retargetable] for the vocabulary these two index, and [Steps.triggeredStep] for the
     * only place the split is reachable from.
     */
    val selfNamed: Phrase<Unit> = constant(Normalizer.SELF, Unit)

    val itPronoun: Phrase<Unit> = constant("it", Unit)

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

    /** The same run with a lowercased initial allowed — see [subtype] for why that is a reading. */
    private val SUBTYPE_PLURAL_ANY_CASE = Regex("""[A-Za-z][A-Za-z-]*s""")

    /** A printed word as a subtype: exact when capitalized, gated to known types when it is not. */
    private fun readSubtype(text: String): Subtype? {
        if (text.isEmpty()) return null
        if (text.first().isUpperCase()) return Subtype(text)
        return Subtype(titleCase(text)).takeIf { it.value in KNOWN_SUBTYPES }
    }

    private fun titleCase(text: String): String = text.replaceFirstChar { it.uppercaseChar() }

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
     * The creature types alone — the ranking set for a bare noun that has to *imply* "creature".
     *
     * Deliberately narrower than [KNOWN_SUBTYPES]: "a Forest" is a land, and [Filters] already
     * spells it as one through the basic-land type nouns. A bare-noun rule that also read it as a
     * creature type would give that phrase two readings with two different models — a hard
     * ambiguity, and one caused entirely by ranking against a list that answers a different
     * question.
     */
    private val CREATURE_SUBTYPES: Set<String> = Subtype.ALL_CREATURE_TYPES.toSet()

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
     * A subtype written in the singular — "Sliver creature", "non-Zombie creature".
     *
     * **Ungated for the printed spelling**, unlike [creatureSubtype] below: this leaf is only ever
     * read in front of a type noun, so the card type comes from the noun and nothing is a guess.
     * There is no de-pluralization either, which is the other thing [pluralSubtype]'s ranking exists
     * for — the word is the type, and the two halves are inverses by construction.
     *
     * ### The lowercased spelling, and why it is gated where the printed one is not
     *
     * A subtype can stand at a **sentence start** — "Sliver creatures get +1/+0." is a whole line,
     * and "{T}, Sacrifice a Goblin: Goblin creatures get +2/+0 …" starts one after the colon — and
     * [com.wingedsheep.assay.syntax.SentenceCase] lowercases every sentence start before the grammar
     * sees it. Undoing that at the text boundary would mean guessing which of a line's sentence
     * starts were proper nouns; doing it here needs no guess at all, because the *word* says.
     *
     * A capital is Oracle telling us this is a type, so it is read as one whatever it is — which is
     * what keeps "Equipment", "Gate" and every other subtype the SDK publishes no list for readable.
     * A lowercase initial carries no such statement: it might be a common noun the sentence-case
     * pass never touched. So that reading is allowed only for a word the SDK *names* as a type,
     * which is the difference between recovering information and inventing it. "artifact creature"
     * consequently has exactly one reading — `ArtifactCreature`, the type noun — rather than two.
     *
     * Printing always writes the capital, so the round trip is unchanged: the leaf's own
     * write-then-read check in [com.wingedsheep.assay.syntax.token] confirms it on every call.
     *
     * The hyphen is in the pattern for the compound creature types the corpus prints; it costs
     * nothing and a type nobody spells simply never appears.
     */
    val subtype: Phrase<Subtype> = token(
        name = "a subtype",
        pattern = Regex("""[A-Za-z][A-Za-z-]*"""),
        read = ::readSubtype,
        write = { it.value },
    )

    /**
     * The same word where it stands **alone** and has to imply "creature" — "target Sliver",
     * "Sacrifice a Goblin", "Slivers can't be blocked".
     *
     * This one *is* ranked against the SDK's creature-type list, and the asymmetry with [subtype] is
     * the point: there the noun supplies the card type, here the word has to. A word the SDK does
     * not name is not a creature type we can claim, and reading one would be the
     * reversible-but-wrong class — "target Scion" round-tripping as a tribe Magic has never had.
     */
    val creatureSubtype: Phrase<Subtype> = token(
        name = "a creature type",
        pattern = Regex("""[A-Za-z][A-Za-z-]*"""),
        read = { readSubtype(it)?.takeIf { s -> s.value in CREATURE_SUBTYPES } },
        write = { it.value.takeIf { v -> v in CREATURE_SUBTYPES } },
    )

    /** …and its plural, which is [pluralSubtype] with the same list applied as a gate. */
    val pluralCreatureSubtype: Phrase<Subtype> = token(
        name = "a creature type",
        pattern = SUBTYPE_PLURAL_ANY_CASE,
        read = { readPluralSubtype(titleCase(it))?.takeIf { s -> s.value in CREATURE_SUBTYPES } },
        write = { subtype -> subtype.takeIf { it.value in CREATURE_SUBTYPES }?.let(::writePluralSubtype) },
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
