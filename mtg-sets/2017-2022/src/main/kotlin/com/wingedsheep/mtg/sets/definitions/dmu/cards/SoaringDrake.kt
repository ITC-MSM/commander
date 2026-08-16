package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Soaring Drake
 * {2}{U}
 * Creature — Drake
 * 2/3
 * Flying
 */
val SoaringDrake = card("Soaring Drake") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Drake"
    oracleText = "Flying"
    power = 2
    toughness = 3

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "66"
        artist = "Jason Kang"
        flavorText = "The already spectacular views from the towers of the College at Lat-Nam are sometimes enhanced by glimpses of drakes diving into the ocean to devour small sharks."
        imageUri = "https://cards.scryfall.io/normal/front/0/b/0b0979f9-aae3-4fc7-beae-c8c6637ae596.jpg?1783921344"
    }
}
