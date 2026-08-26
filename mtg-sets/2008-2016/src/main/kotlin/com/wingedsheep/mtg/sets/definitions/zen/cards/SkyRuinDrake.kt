package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sky Ruin Drake
 * {4}{U}
 * Creature — Drake
 * 2/5
 * Flying
 */
val SkyRuinDrake = card("Sky Ruin Drake") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Drake"
    power = 2
    toughness = 5
    oracleText = "Flying"

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "66"
        artist = "Izzy"
        flavorText = "\"Hold up your spears. And try not to look like food.\"\n—Tarsa, Sea Gate sell-sword"
        imageUri = "https://cards.scryfall.io/normal/front/2/b/2bdb5850-df1e-4d8a-af7a-15cab080fb8f.jpg"
    }
}
