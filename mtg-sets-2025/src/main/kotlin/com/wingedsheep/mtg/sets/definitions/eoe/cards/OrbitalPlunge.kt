package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Orbital Plunge
 * {3}{R}
 * Sorcery
 *
 * Orbital Plunge deals 6 damage to target creature. If excess damage was dealt
 * this way, create a Lander token. (It's an artifact with "{2}, {T}, Sacrifice
 * this token: Search your library for a basic land card, put it onto the
 * battlefield tapped, then shuffle.")
 *
 * Composed from existing atoms — [Effects.DealDamage] writes marked damage to the
 * target, then [Conditions.IfTargetTookExcessDamage] reads the post-damage state to
 * gate the Lander payoff. No bespoke "excess damage detection" executor needed.
 */
val OrbitalPlunge = card("Orbital Plunge") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Orbital Plunge deals 6 damage to target creature. If excess damage was dealt " +
        "this way, create a Lander token. (It's an artifact with \"{2}, {T}, Sacrifice this " +
        "token: Search your library for a basic land card, put it onto the battlefield tapped, " +
        "then shuffle.\")"

    spell {
        val damaged = target("creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.DealDamage(6, damaged),
            ConditionalEffect(
                condition = Conditions.IfTargetTookExcessDamage(),
                effect = Effects.CreateLander(),
            ),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "149"
        artist = "Inkognit"
        flavorText = "The Kav pilot gleefully ignored his lander's collision warning."
        imageUri = "https://cards.scryfall.io/normal/front/2/d/2dc7cc17-5319-4694-99c6-8c56a0b40a44.jpg?1752947156"
    }
}
