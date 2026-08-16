package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Preordain
 * {U}
 * Sorcery
 * Scry 2, then draw a card. (To scry 2, look at the top two cards of your library, then put any number of them on the bottom and the rest on top in any order.)
 */
val Preordain = card("Preordain") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Scry 2, then draw a card. (To scry 2, look at the top two cards of your library, then put any number of them on the bottom and the rest on top in any order.)"

    spell {
        effect = Effects.Composite(
            Effects.Scry(2),
            Effects.DrawCards(1),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "70"
        artist = "Svetlin Velinov"
        imageUri = "https://cards.scryfall.io/normal/front/e/3/e3868c3d-4fcd-444b-866f-0f8e50ce7b67.jpg?1783941822"
    }
}
