package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Seacoast Drake
 * {1}{U}
 * Creature — Drake
 * 1 / 3
 * Flying
 */
val SeacoastDrake = card("Seacoast Drake") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Drake"
    power = 1
    toughness = 3
    oracleText = "Flying"

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "70"
        artist = "Scott Chou"
        flavorText = "Seacoast drakes have been known to follow ships for hundreds of miles, waiting to snap up garbage, bait, and the occasional sailor."
        imageUri = "https://cards.scryfall.io/normal/front/5/3/5333de10-a6d4-47ff-ab57-4edb49535739.jpg"
    }
}
