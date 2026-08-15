package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Polymorph reprint in Magic 2010.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Mirage's
 * `cards/` package; this file contributes only the M10-specific presentation row.
 */
val PolymorphReprint = Printing(
    oracleId = "039115f2-6322-4674-bc64-f21883ed375a",
    name = "Polymorph",
    setCode = "M10",
    collectorNumber = "67",
    scryfallId = "83d274f4-452b-4ff2-b026-83667e9ba98f",
    artist = "Robert Bliss",
    imageUri = "https://cards.scryfall.io/normal/front/8/3/83d274f4-452b-4ff2-b026-83667e9ba98f.jpg?1721690862",
    releaseDate = "2009-07-17",
    rarity = Rarity.RARE,
)
