package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Pirate's Prize
 * {3}{U}
 * Sorcery
 *
 * Draw two cards. Create a Treasure token.
 */
val PiratesPrize = card("Pirate's Prize") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Draw two cards. Create a Treasure token. (It's an artifact with " +
        "\"{T}, Sacrifice this token: Add one mana of any color.\")"

    spell {
        effect = Effects.DrawCards(2) then Effects.CreateTreasure()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "68"
        artist = "Deruchenko Alexander"
        flavorText = "Nothing warms the heart like plunder."
        imageUri = "https://cards.scryfall.io/normal/front/4/8/48d97373-9b55-4eb7-99b6-8912f09bd0bb.jpg"
    }
}
