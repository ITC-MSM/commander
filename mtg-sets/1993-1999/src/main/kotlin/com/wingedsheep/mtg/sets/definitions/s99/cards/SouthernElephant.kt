package com.wingedsheep.mtg.sets.definitions.s99.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Southern Elephant reprint in S99.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * PTK's `cards/` package (the card's earliest real printing). This file
 * contributes only the S99-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SouthernElephantReprint = Printing(
    oracleId = "79de2319-078d-46db-9044-0088c623b73f",
    name = "Southern Elephant",
    setCode = "S99",
    collectorNumber = "142",
    artist = "Wang Yuqun",
    imageUri = "https://cards.scryfall.io/normal/front/7/7/77c3a2b3-5ad8-4b16-a7a8-8344b78ca77b.jpg?1783946020",
    releaseDate = "1999-07-01",
    rarity = Rarity.COMMON,
)
