package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hop to It
 * {2}{W}
 * Sorcery
 *
 * Create three 1/1 white Rabbit creature tokens.
 */
val HopToIt = card("Hop to It") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Create three 1/1 white Rabbit creature tokens."

    spell {
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Rabbit"),
            count = 3,
            imageUri = "https://cards.scryfall.io/normal/front/8/1/81de52ef-7515-4958-abea-fb8ebdcef93c.jpg?1721431122"
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "16"
        artist = "Eelis Kyttanen"
        flavorText = "\"Finding a hidden patch of glowberries is almost as joyful as eating them. Almost.\"\n—Ms. Bumbleflower"
        imageUri = "https://cards.scryfall.io/normal/front/e/e/ee7207f8-5daa-42af-aeea-7a489047110b.jpg?1721425854"
    }
}
