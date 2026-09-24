package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Violent Echoes
 * {2}{R}{R}
 * Instant
 *
 * Violent Echoes deals 6 damage to target creature or planeswalker. If excess damage was dealt to
 * that permanent this way, empower Jace X, where X is that excess damage.
 *
 * The damage step stores its excess (CR 120.4a — above lethal for a creature, above loyalty for a
 * planeswalker) in a pipeline number via `excessDamageVariable`; the empower is gated on it being
 * positive, so no excess means no Jace token is created.
 */
val ViolentEchoes = card("Violent Echoes") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Violent Echoes deals 6 damage to target creature or planeswalker. If excess damage " +
        "was dealt to that permanent this way, empower Jace X, where X is that excess damage. (Put " +
        "that many loyalty counters on a Jace token you control. If you don't control one, first " +
        "create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")"

    spell {
        val permanent = target("target creature or planeswalker", Targets.CreatureOrPlaneswalker)
        val excess = DynamicAmount.VariableReference("violentEchoesExcess")
        effect = Effects.Composite(
            Effects.DealDamage(6, permanent, excessDamageVariable = "violentEchoesExcess"),
            ConditionalEffect(
                condition = Conditions.CompareAmounts(excess, ComparisonOperator.GT, DynamicAmount.Fixed(0)),
                effect = Patterns.Mechanic.empowerJace(excess),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "95"
        artist = "Andrea Piparo"
        flavorText = "Two Multiverses. Only one future."
        imageUri = "https://cards.scryfall.io/normal/front/a/d/ad03ba90-2442-4a71-94df-2088b5b63662.jpg?1789127599"
        inBooster = false
    }
}
