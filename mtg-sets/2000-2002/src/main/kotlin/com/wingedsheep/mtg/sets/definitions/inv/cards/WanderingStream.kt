package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Wandering Stream
 * {2}{G}
 * Sorcery
 * Domain — You gain 2 life for each basic land type among lands you control.
 */
val WanderingStream = card("Wandering Stream") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Domain — You gain 2 life for each basic land type among lands you control."

    spell {
        // 2 life per basic land type = domain × 2.
        effect = Effects.GainLife(DynamicAmount.Multiply(DynamicAmounts.domain(), 2))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "224"
        artist = "Quinton Hoover"
        flavorText = "\"Dominaria touches us all.\"\n—Molimo, maro-sorcerer"
        imageUri = "https://cards.scryfall.io/normal/front/6/d/6da5cb6c-253b-44f0-98f9-d75f42c6e14b.jpg?1562916978"
    }
}
