package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Thornweald Archer reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * FUT's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ThornwealdArcherReprint = Printing(
    oracleId = "b5ba87f5-3589-4a8b-ad65-2ae2523e98e8",
    name = "Thornweald Archer",
    setCode = "FDN",
    collectorNumber = "559",
    artist = "Dave Kendall",
    imageUri = "https://cards.scryfall.io/normal/front/1/8/189f6199-f2fe-49a5-89ca-3c4cb39fbf2b.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
