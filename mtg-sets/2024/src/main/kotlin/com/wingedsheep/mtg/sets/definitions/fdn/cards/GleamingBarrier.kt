package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Gleaming Barrier reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * RIX's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GleamingBarrierReprint = Printing(
    oracleId = "1eb9e401-8975-447b-839d-f7cd23897465",
    name = "Gleaming Barrier",
    setCode = "FDN",
    collectorNumber = "252",
    artist = "Jason Felix",
    imageUri = "https://cards.scryfall.io/normal/front/1/b/1b49b009-e6f2-494a-9235-f5c25c2d70a9.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
