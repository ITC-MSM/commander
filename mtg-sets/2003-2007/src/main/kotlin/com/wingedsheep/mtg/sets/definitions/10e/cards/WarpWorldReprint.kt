package com.wingedsheep.mtg.sets.definitions.`10e`.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Warp World reprint in 10E.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in RAV's `cards/` package (the
 * card's earliest real printing). This file contributes only the 10E-specific presentation row.
 */
val WarpWorldReprint = Printing(
    oracleId = "50a228a2-c8b6-4416-b0ab-417926a9b9b6",
    name = "Warp World",
    setCode = "10E",
    collectorNumber = "248",
    scryfallId = "c0df0a08-7f62-4b8a-a37d-c778c07076c6",
    artist = "Ron Spencer",
    imageUri = "https://cards.scryfall.io/normal/front/c/0/c0df0a08-7f62-4b8a-a37d-c778c07076c6.jpg",
    releaseDate = "2007-07-13",
    rarity = Rarity.RARE,
)
