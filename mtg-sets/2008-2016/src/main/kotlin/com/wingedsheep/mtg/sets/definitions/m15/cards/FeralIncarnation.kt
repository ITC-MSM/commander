package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Feral Incarnation
 * {8}{G}
 * Sorcery
 * Convoke
 * Create three 3/3 green Beast creature tokens.
 */
val FeralIncarnation = card("Feral Incarnation") {
    manaCost = "{8}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText =
        "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Create three 3/3 green Beast creature tokens."

    keywords(Keyword.CONVOKE)

    spell {
        effect = Effects.CreateToken(
            power = 3,
            toughness = 3,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Beast"),
            count = 3,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "174"
        artist = "Eytan Zana"
        flavorText = "Nature is itself wild—in all its forms."
        imageUri = "https://cards.scryfall.io/normal/front/d/8/d8c0c7e7-81f5-4e75-8120-95488fd6ff60.jpg?1783939167"
    }
}
