package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Dragon Fodder
 * {1}{R}
 * Sorcery
 * Create two 1/1 red Goblin creature tokens.
 *
 * Canonical definition lives in Shards of Alara (earliest real printing).
 * Reprinted in Dragons of Tarkir — see DTK `DragonFodderReprint`.
 */
val DragonFodder = card("Dragon Fodder") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Create two 1/1 red Goblin creature tokens."

    spell {
        effect = CreateTokenEffect(
            count = 2,
            power = 1,
            toughness = 1,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Goblin"),
            imageUri = "https://cards.scryfall.io/normal/front/e/d/ed418a8b-f158-492d-a323-6265b3175292.jpg?1562640121"
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "97"
        artist = "Jaime Jones"
        flavorText = "Goblins journey to the sacrificial peaks in pairs so that the rare survivor might be able to relate the details of the other's grisly demise."
        imageUri = "https://cards.scryfall.io/normal/front/9/e/9eab4120-e7d8-4132-a304-30b88e3175e2.jpg?1562707210"
    }
}
