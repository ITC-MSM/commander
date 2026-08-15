package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Drogskol Reaver reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * DKA's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DrogskolReaverReprint = Printing(
    oracleId = "68175b49-f1e6-4b34-aad9-20d61a43d427",
    name = "Drogskol Reaver",
    setCode = "FDN",
    collectorNumber = "655",
    artist = "Vincent Proce",
    imageUri = "https://cards.scryfall.io/normal/front/c/0/c0a4450c-8dfd-4d05-adb9-e84ec6066c7d.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
