package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Hoarding Dragon reprint in Magic 2015.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in M11's `cards/` package
 * (the card's earliest real printing). This file contributes only the M15 presentation row.
 */
val HoardingDragonReprint = Printing(
    oracleId = "d36d3c93-ed24-4447-9d15-3071f5c0989b",
    name = "Hoarding Dragon",
    setCode = "M15",
    collectorNumber = "149",
    scryfallId = "7c4c4a0f-0ee4-422d-b807-f64b77dd6831",
    artist = "Matt Cavotta",
    imageUri = "https://cards.scryfall.io/normal/front/7/c/7c4c4a0f-0ee4-422d-b807-f64b77dd6831.jpg?1782713199",
    releaseDate = "2014-07-18",
    rarity = Rarity.RARE,
)
