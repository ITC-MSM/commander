package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ral's Reinforcements
 * {1}{R}
 * Sorcery
 * Create two 1/1 blue and red Elemental creature tokens.
 */
val RalsReinforcements = card("Ral's Reinforcements") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Create two 1/1 blue and red Elemental creature tokens."

    spell {
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLUE, Color.RED),
            creatureTypes = setOf("Elemental"),
            count = 2,
            imageUri = "https://cards.scryfall.io/normal/front/2/8/28a7a9b0-d823-4b34-829f-ade81fc141e0.jpg?1783916668"
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "158"
        artist = "Nils Hamm"
        flavorText = "\"Good thing maintenance never got around to decatalyzing the bithermic " +
            "plasma displacement conduits!\""
        imageUri = "https://cards.scryfall.io/normal/front/7/4/74d0bb76-075c-49d7-9afb-e5bcf5b654f7.jpg?1783916984"
    }
}
