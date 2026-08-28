package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Moaning Spirit
 * {2}{B}
 * Creature — Spirit
 * 2/1
 * Flying
 */
val MoaningSpirit = card("Moaning Spirit") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Spirit"
    oracleText = "Flying"
    power = 2
    toughness = 1
    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "79"
        artist = "Rebecca Guay"
        flavorText = "The dead sing their own lullabies."
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cdd99210-5201-4ecc-b86a-aee9dafe2657.jpg"
    }
}
