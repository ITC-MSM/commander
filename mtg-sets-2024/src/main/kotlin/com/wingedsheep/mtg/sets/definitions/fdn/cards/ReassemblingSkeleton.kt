package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Reassembling Skeleton reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ARC's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ReassemblingSkeletonReprint = Printing(
    oracleId = "9dbc3530-b278-4c8d-b2cc-a09dfac9d5e5",
    name = "Reassembling Skeleton",
    setCode = "FDN",
    collectorNumber = "182",
    artist = "Austin Hsu",
    imageUri = "https://cards.scryfall.io/normal/front/2/8/28e84b1b-1c05-4e1b-93b8-9cc2ca73509d.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
