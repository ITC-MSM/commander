package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Warp World reprint in M10.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in RAV's `cards/` package (the
 * card's earliest real printing). This file contributes only the M10-specific presentation row.
 */
val WarpWorldReprint = Printing(
    oracleId = "50a228a2-c8b6-4416-b0ab-417926a9b9b6",
    name = "Warp World",
    setCode = "M10",
    collectorNumber = "163",
    scryfallId = "aa6e1fb5-a06b-4e10-8cc7-785e0f0b298e",
    artist = "Ron Spencer",
    imageUri = "https://cards.scryfall.io/normal/front/a/a/aa6e1fb5-a06b-4e10-8cc7-785e0f0b298e.jpg",
    releaseDate = "2009-07-17",
    rarity = Rarity.RARE,
)
