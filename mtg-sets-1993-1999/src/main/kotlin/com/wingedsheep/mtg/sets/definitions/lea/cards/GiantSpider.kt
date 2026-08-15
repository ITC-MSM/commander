package com.wingedsheep.mtg.sets.definitions.lea.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Giant Spider
 * {3}{G}
 * Creature — Spider
 * 2/4
 * Reach
 */
val GiantSpider = card("Giant Spider") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Spider"
    power = 2
    toughness = 4

    keywords(Keyword.REACH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "198"
        artist = "Sandra Everingham"
        imageUri = "https://cards.scryfall.io/normal/front/7/7/77636b4c-faea-4bf5-b88c-dd5bb88dc930.jpg?1559591693"
    }
}
