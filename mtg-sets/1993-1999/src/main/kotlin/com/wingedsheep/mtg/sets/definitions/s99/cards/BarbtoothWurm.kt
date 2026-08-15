package com.wingedsheep.mtg.sets.definitions.s99.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Barbtooth Wurm reprint in S99.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * P02's `cards/` package (the card's earliest real printing). This file
 * contributes only the S99-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BarbtoothWurmReprint = Printing(
    oracleId = "03ad624e-71f6-4f72-8ce0-7700e83458d9",
    name = "Barbtooth Wurm",
    setCode = "S99",
    collectorNumber = "125",
    artist = "Rebecca Guay",
    imageUri = "https://cards.scryfall.io/normal/front/e/8/e85fbc25-412a-4367-8209-258ff638dcc6.jpg?1783946024",
    releaseDate = "1999-07-01",
    rarity = Rarity.COMMON,
)
