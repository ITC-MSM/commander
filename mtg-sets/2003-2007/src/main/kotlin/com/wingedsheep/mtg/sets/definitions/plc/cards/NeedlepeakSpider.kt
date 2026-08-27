package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Needlepeak Spider
 * {3}{R}
 * Creature — Spider
 * 4/2
 * Reach
 */
val NeedlepeakSpider = card("Needlepeak Spider") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Spider"
    power = 4
    toughness = 2
    oracleText = "Reach"

    keywords(Keyword.REACH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "105"
        artist = "Dany Orizio"
        flavorText = "\"It's a testament to the forests' devastation that giant spiders now make their homes amid Dominaria's barren spires.\"\n—Aznaph, greenseeker"
        imageUri = "https://cards.scryfall.io/normal/front/6/a/6a4307e3-3138-4ccb-8aa3-2ffcfeb2948f.jpg"
    }
}
