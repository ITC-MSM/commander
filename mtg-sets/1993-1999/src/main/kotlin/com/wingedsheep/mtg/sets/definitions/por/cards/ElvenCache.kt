package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Elven Cache reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * VIS's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ElvenCacheReprint = Printing(
    oracleId = "8a922366-ee2a-4ddb-a904-683dcf4f191a",
    name = "Elven Cache",
    setCode = "POR",
    collectorNumber = "164",
    artist = "Rebecca Guay",
    imageUri = "https://cards.scryfall.io/normal/front/6/8/68939020-eb6a-4d77-a850-4df96cf01918.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.COMMON,
)
