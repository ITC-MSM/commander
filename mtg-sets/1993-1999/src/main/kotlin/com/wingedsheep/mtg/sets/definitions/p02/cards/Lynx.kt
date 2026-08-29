package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Lynx
 * {1}{G}
 * Creature — Cat
 * 2/1
 * Forestwalk (This creature can't be blocked as long as defending player controls a Forest.)
 */
val Lynx = card("Lynx") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Cat"
    oracleText = "Forestwalk (This creature can't be blocked as long as defending player controls a Forest.)"
    power = 2
    toughness = 1
    keywords(Keyword.FORESTWALK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "132"
        artist = "Rebecca Guay"
        flavorText = "Rarely seen, hardly heard, never caught."
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2e8648e1-bc40-4eff-ad7b-ab0b62a47570.jpg"
    }
}
