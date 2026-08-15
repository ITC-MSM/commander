package com.wingedsheep.mtg.sets.definitions.m13.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Kraken Hatchling reprint in M13.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * ZEN's `cards/` package (the card's earliest real printing). This file
 * contributes only the M13-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val KrakenHatchlingReprint = Printing(
    oracleId = "08e6c786-d553-4c23-9137-cfad84146739",
    name = "Kraken Hatchling",
    setCode = "M13",
    collectorNumber = "58",
    artist = "Jason Felix",
    imageUri = "https://cards.scryfall.io/normal/front/5/9/59a50590-9091-4632-bf8c-792e1e0a75a8.jpg?1783940505",
    releaseDate = "2012-07-13",
    rarity = Rarity.COMMON,
)
