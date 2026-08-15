package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
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
     * A creature type written in its plural surface form — "protection from **Goblins**".
     *
     * The plural lives in the leaf rather than in a template literal because a `{subtype}` slot
     * followed by a literal `"s"` would let the slot swallow the "s" and strand the literal.
     *
     * Irregular plurals are listed explicitly: "Elves" naively de-pluralizes to `Elve`, which
     * *round-trips perfectly* while meaning nothing. That is the reversible-but-wrong class the
     * touchstone structurally cannot catch — the differential gate (Phase 3) is the general
     * answer, and five map entries are the cheap one here.
     */
    val pluralSubtype: Phrase<Subtype> = token(
        name = "a creature type",
        pattern = Regex("""[A-Z][A-Za-z-]*s"""),
        read = { plural -> IRREGULAR_PLURALS[plural]?.let(::Subtype) ?: Subtype(plural.dropLast(1)) },
        write = { subtype ->
            IRREGULAR_PLURALS.entries.firstOrNull { it.value == subtype.value }?.key ?: "${subtype.value}s"
        },
    )

    /**
     * "white", or "white and from blue" — one rule rather than two, so a single colour has exactly
     * one reading. Splitting it would hand the same text two distinct models
     * ([ProtectionScope.Color] versus a one-element [ProtectionScope.Colors]), which is the
     * ambiguity hard error rather than a choice to make.
     */
    private val colorScope: Phrase<ProtectionScope> = phrase("{colors}", name = "a colour") {
        slot("colors", separated("colours", color, " and from "))
        build {
            val colors: List<Color> = it.value("colors")
            if (colors.size == 1) ProtectionScope.Color(colors.single()) else ProtectionScope.Colors(colors.toSet())
        }
        match {
            when (it) {
                is ProtectionScope.Color -> bind("colors" to listOf(it.color))
                is ProtectionScope.Colors -> bind("colors" to it.colors.toList())
                else -> null
            }
        }
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

    private val IRREGULAR_PLURALS = mapOf(
        "Elves" to "Elf",
        "Dwarves" to "Dwarf",
        "Wolves" to "Wolf",
        "Thieves" to "Thief",
        "Scarecrows" to "Scarecrow",
    )
}
