package com.wingedsheep.mtg.sets.definitions.lea.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Grizzly Bears
 * {1}{G}
 * Creature — Bear
 * 2/2
 */
val GrizzlyBears = card("Grizzly Bears") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Bear"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "199"
        artist = "Jeff A. Menges"
        imageUri = "https://cards.scryfall.io/normal/front/c/e/ce2d603a-3231-4a8c-bf39-1617586ea870.jpg?1559591691"
    }
}
