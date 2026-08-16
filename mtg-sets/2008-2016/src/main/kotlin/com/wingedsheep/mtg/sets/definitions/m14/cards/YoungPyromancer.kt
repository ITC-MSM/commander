package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Young Pyromancer
 * {1}{R}
 * Creature — Human Shaman
 * 2/1
 * Whenever you cast an instant or sorcery spell, create a 1/1 red Elemental creature token.
 */
val YoungPyromancer = card("Young Pyromancer") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Shaman"
    power = 2
    toughness = 1
    oracleText = "Whenever you cast an instant or sorcery spell, create a 1/1 red Elemental creature token."

    triggeredAbility {
        trigger = Triggers.YouCastInstantOrSorcery
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Elemental")
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "163"
        artist = "Cynthia Sheppard"
        flavorText = "Immolation is the sincerest form of flattery."
        imageUri = "https://cards.scryfall.io/normal/front/e/3/e349c204-3a93-4bf7-b79a-5f5f261ea2d3.jpg?1783939908"
        ruling(
            "2021-03-19",
            "An ability that triggers when a player casts a spell resolves before the spell that caused " +
                "it to trigger, but after targets have been chosen for that spell. It resolves even if " +
                "that spell is countered."
        )
    }
}
