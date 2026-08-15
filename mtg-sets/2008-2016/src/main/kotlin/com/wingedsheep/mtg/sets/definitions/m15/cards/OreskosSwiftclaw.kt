package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Oreskos Swiftclaw reprint in M15.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * JOU's `cards/` package (the card's earliest real printing). This file
 * contributes only the M15-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val OreskosSwiftclawReprint = Printing(
    oracleId = "014d00fc-434a-4b06-84b2-afd930677d61",
    name = "Oreskos Swiftclaw",
    setCode = "M15",
    collectorNumber = "22",
    artist = "James Ryman",
    imageUri = "https://cards.scryfall.io/normal/front/2/d/2d32e198-8ddd-44bc-be57-17e1ff72d666.jpg?1783939200",
    releaseDate = "2014-07-18",
    rarity = Rarity.COMMON,
)
