package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Warpath Ghoul reprint in M12.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M10's `cards/` package (the card's earliest real printing). This file
 * contributes only the M12-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val WarpathGhoulReprint = Printing(
    oracleId = "17ae652c-b1c7-4dde-ae9e-50b9e286776c",
    name = "Warpath Ghoul",
    setCode = "M12",
    collectorNumber = "117",
    artist = "rk post",
    imageUri = "https://cards.scryfall.io/normal/front/9/4/94785274-fa79-47cc-9896-0f5f695abb21.jpg?1783941076",
    releaseDate = "2011-07-15",
    rarity = Rarity.COMMON,
)
