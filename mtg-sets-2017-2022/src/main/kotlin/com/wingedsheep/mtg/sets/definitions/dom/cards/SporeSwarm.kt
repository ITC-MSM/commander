package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Spore Swarm
 * {3}{G}
 * Instant
 * Create three 1/1 green Saproling creature tokens.
 */
val SporeSwarm = card("Spore Swarm") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Create three 1/1 green Saproling creature tokens."

    spell {
        effect = CreateTokenEffect(
            count = 3,
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Saproling"),
            imageUri = "https://cards.scryfall.io/normal/front/5/3/5371de1b-db33-4db4-a518-e35c71aa72b7.jpg?1562702067"
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "180"
        artist = "Mathias Kollros"
        flavorText = "\"As the irrepressible power of a dormant Multani courses through Yavimaya, the forest passes judgment on travelers and natives alike. Only the fungus prospers.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/2/b2314215-23af-4c8e-860f-b029e151af36.jpg?1562741444"
    }
}
