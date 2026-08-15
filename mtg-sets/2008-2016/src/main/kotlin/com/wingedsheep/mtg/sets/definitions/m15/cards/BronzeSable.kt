package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Bronze Sable reprint in M15.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * THS's `cards/` package (the card's earliest real printing). This file
 * contributes only the M15-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BronzeSableReprint = Printing(
    oracleId = "f27e5e11-0ad0-448b-8760-75ed1b97e7d8",
    name = "Bronze Sable",
    setCode = "M15",
    collectorNumber = "214",
    artist = "Jasper Sandner",
    imageUri = "https://cards.scryfall.io/normal/front/e/8/e8ba6d7d-fad0-4af1-be12-44be326a031e.jpg?1783939159",
    releaseDate = "2014-07-18",
    rarity = Rarity.COMMON,
)
