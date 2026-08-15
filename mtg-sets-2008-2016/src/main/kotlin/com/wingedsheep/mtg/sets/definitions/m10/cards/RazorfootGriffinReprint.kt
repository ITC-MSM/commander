package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Razorfoot Griffin reprint in Magic 2010.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Invasion's
 * `cards/` package; this file contributes only the M10-specific presentation row.
 */
val RazorfootGriffinReprint = Printing(
    oracleId = "c7ecb785-2bfc-4953-b356-db7c27406dbb",
    name = "Razorfoot Griffin",
    setCode = "M10",
    collectorNumber = "25",
    scryfallId = "9dc4d69c-f61d-4122-9e0b-c88aa905d159",
    artist = "Ben Thompson",
    imageUri = "https://cards.scryfall.io/normal/front/9/d/9dc4d69c-f61d-4122-9e0b-c88aa905d159.jpg?1561990173",
    releaseDate = "2009-07-17",
    rarity = Rarity.COMMON,
)
