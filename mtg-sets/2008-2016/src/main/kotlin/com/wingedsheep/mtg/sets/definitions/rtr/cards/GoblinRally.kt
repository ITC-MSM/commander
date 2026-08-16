package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Goblin Rally
 * {3}{R}{R}
 * Sorcery
 * Create four 1/1 red Goblin creature tokens.
 */
val GoblinRally = card("Goblin Rally") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Create four 1/1 red Goblin creature tokens."

    spell {
        effect = Effects.CreateToken(
            count = 4,
            power = 1,
            toughness = 1,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Goblin"),
            imageUri = "https://cards.scryfall.io/normal/front/e/d/ed418a8b-f158-492d-a323-6265b3175292.jpg?1562640121"
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "95"
        artist = "Nic Klein"
        flavorText = "You don't so much hire goblins as put ideas in their heads."
        imageUri = "https://cards.scryfall.io/normal/front/e/4/e4ec8ada-09a6-449a-ac4a-7d3acbd08014.jpg?1783940356"
    }
}
