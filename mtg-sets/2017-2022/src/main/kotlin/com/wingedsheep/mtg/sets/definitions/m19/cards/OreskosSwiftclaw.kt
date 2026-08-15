package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Oreskos Swiftclaw reprint in M19.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * JOU's `cards/` package (the card's earliest real printing). This file
 * contributes only the M19-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val OreskosSwiftclawReprint = Printing(
    oracleId = "014d00fc-434a-4b06-84b2-afd930677d61",
    name = "Oreskos Swiftclaw",
    setCode = "M19",
    collectorNumber = "31",
    artist = "James Ryman",
    imageUri = "https://cards.scryfall.io/normal/front/0/e/0ea1dfb4-1983-41f7-956c-f2a1d1489b54.jpg?1783934600",
    releaseDate = "2018-07-13",
    rarity = Rarity.COMMON,
)
