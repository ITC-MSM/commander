package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.constant
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.TurnTracker
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * "Create a 1/1 green Insect creature token." — the token clauses.
 *
 * One shape with three slots (the stats, the colour, the creature type) and a rule per *count*
 * surface, because the count is where the English differs: "a" is one token, "X" is the spell's own
 * variable, and "for each …" is a tally. Everything before the word "token" is the same phrase in
 * all three.
 *
 * ### The colour word is a rule, not [Primitives.color]
 *
 * A token's colours are a `Set<Color>`, and "colorless" is the empty set rather than a colour — a
 * distinction [Primitives.color] cannot make, since it reads exactly one [Color]. One alternation
 * over "colorless" plus the five colour words keeps both halves total, and a multicoloured token
 * ("a 1/1 white and blue Spirit") is a printed form nobody wrote down here, so it declines.
 *
 * ### `imageUri` is not in the text, and this is where that shows
 *
 * `CreateTokenEffect` carries an `imageUri` that no printed word determines — it is art, chosen when
 * the card was authored. The rules here build without one, so a hand-written card that names its
 * token's art round-trips its *text* perfectly and shows up as a differential divergence. That is
 * the honest split: the touchstone is about the text and the field is not in it, so the finding
 * belongs to the gate that compares models rather than to a rule that would have to invent a URL.
 */
object Tokens {

    /** "colorless", "green" — a token's colour set, which is empty for the colourless case. */
    private val colours: Phrase<Set<Color>> = oneOf(
        "a token's colour",
        constant("colorless", emptySet()),
        *Color.entries.map { constant(it.displayName.lowercase(), setOf(it)) }.toTypedArray(),
    )

    /**
     * The shape: "create <count> P/T <colour> <type> creature token(s)".
     *
     * [count] is the whole of what varies between the members — the article and the noun's number
     * change with it, so each member spells its own template rather than sharing one with a number
     * slot.
     */
    private fun createToken(
        template: String,
        name: String,
        count: DynamicAmount,
    ): Phrase<CardScript> {
        fun scriptFor(power: Int, toughness: Int, colours: Set<Color>, type: Subtype) = CardScript(
            spellEffect = Effects.CreateToken(
                count = count,
                power = power,
                toughness = toughness,
                colors = colours,
                creatureTypes = setOf(type.value),
            )
        )
        return phrase(template, name = name) {
            slot("p", Primitives.cardinal)
            slot("t", Primitives.cardinal)
            slot("color", colours)
            slot("type", Primitives.subtype)
            build { scriptFor(it.int("p"), it.int("t"), it.value("color"), it.value("type")) }
            match { script ->
                val token = script.spellEffect as? CreateTokenEffect ?: return@match null
                val type = token.creatureTypes.singleOrNull() ?: return@match null
                if (script != scriptFor(token.power, token.toughness, token.colors, Subtype(type))) {
                    return@match null
                }
                bind(
                    "p" to token.power,
                    "t" to token.toughness,
                    "color" to token.colors,
                    "type" to Subtype(type),
                )
            }
        }
    }

    /** One token clause, for the sentences that wrap it — see [Granted]. */
    val clause: Phrase<CardScript> get() = oneOf("a token clause", clauses)

    val clauses: List<Phrase<CardScript>> = listOf(
        createToken(
            "create a {p}/{t} {color} {type} creature token",
            "create a token",
            DynamicAmount.Fixed(1),
        ),
        createToken(
            "create X {p}/{t} {color} {type} creature tokens",
            "create X tokens",
            DynamicAmount.XValue,
        ),
        // Caller of the Claw. The tally is a *turn* tracker rather than a battlefield count, which
        // is why it is a rule here and not a row in [Amounts.count]: nothing about the phrase is a
        // noun the filter vocabulary could spell, and the whole clause names one tracked quantity.
        createToken(
            "create a {p}/{t} {color} {type} creature token for each nontoken creature put into " +
                "your graveyard from the battlefield this turn",
            "create a token per creature that died this turn",
            DynamicAmount.TurnTracking(Player.You, TurnTracker.NONTOKEN_CREATURES_DIED),
        ),
    )
}
