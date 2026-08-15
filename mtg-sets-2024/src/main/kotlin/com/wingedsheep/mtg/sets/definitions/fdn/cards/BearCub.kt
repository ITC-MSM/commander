package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Bear Cub reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * P02's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BearCubReprint = Printing(
    oracleId = "ed206651-3d9b-4546-8d45-0682817192fd",
    name = "Bear Cub",
    setCode = "FDN",
    collectorNumber = "552",
    artist = "Ron Spencer",
    imageUri = "https://cards.scryfall.io/normal/front/d/8/d8662ebb-068b-41d2-b504-4b5854e4d4aa.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
