package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Moorish Cavalry
 * {2}{W}{W}
 * Creature — Human Knight
 * 3/3
 * Trample
 */
val MoorishCavalry = card("Moorish Cavalry") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    power = 3
    toughness = 3
    oracleText = "Trample"
    keywords(Keyword.TRAMPLE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "7"
        artist = "Dameon Willich"
        flavorText = "Members of the elite Moorish Cavalry are very particular about their mounts, choosing only those whose bloodlines have been pure for generations."
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f86f0781-7614-4779-a58d-f13ce96bdf33.jpg?1562941680"
    }
}
