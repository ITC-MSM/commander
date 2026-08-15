package com.wingedsheep.mtg.sets.definitions.lea.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Merfolk of the Pearl Trident
 * {U}
 * Creature — Merfolk
 * 1/1
 */
val MerfolkOfThePearlTrident = card("Merfolk of the Pearl Trident") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk"
    power = 1
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "66"
        artist = "Jeff A. Menges"
        imageUri = "https://cards.scryfall.io/normal/front/2/b/2b871039-6a66-4ac3-95e7-24759c1f2f92.jpg?1559591367"
    }
}
