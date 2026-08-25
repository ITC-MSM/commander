package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sprout
 * {G}
 * Instant
 * Create a 1/1 green Saproling creature token.
 */
val Sprout = card("Sprout") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Create a 1/1 green Saproling creature token."

    spell {
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Saproling")
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "221"
        artist = "Anthony S. Waters"
        flavorText = "Centuries of temporal strife had stripped Dominaria of its natural defenses, but nature fought back with armies constructed of little more than grime and sunlight."
        imageUri = "https://cards.scryfall.io/normal/front/6/9/6967363f-1e05-4484-8790-47f7be68455c.jpg"
    }
}
