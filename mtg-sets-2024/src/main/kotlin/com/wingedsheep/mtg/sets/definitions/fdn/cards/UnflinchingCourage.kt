package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Unflinching Courage reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * DGM's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val UnflinchingCourageReprint = Printing(
    oracleId = "0e969a27-1609-4ab4-b0db-46b1af8066a9",
    name = "Unflinching Courage",
    setCode = "FDN",
    collectorNumber = "722",
    artist = "Mike Bierek",
    imageUri = "https://cards.scryfall.io/normal/front/a/1/a1201875-b3d8-4f95-801e-bf18ee77919b.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
