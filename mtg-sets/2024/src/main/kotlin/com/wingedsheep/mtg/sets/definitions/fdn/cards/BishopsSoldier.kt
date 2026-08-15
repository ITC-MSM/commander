package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Bishop's Soldier reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * XLN's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BishopsSoldierReprint = Printing(
    oracleId = "84487586-4f71-457a-b983-4f1f7a71deef",
    name = "Bishop's Soldier",
    setCode = "FDN",
    collectorNumber = "491",
    artist = "Scott Murphy",
    imageUri = "https://cards.scryfall.io/normal/front/1/6/16dfd7b3-6d01-4e98-aec3-b27e8e2444e8.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
