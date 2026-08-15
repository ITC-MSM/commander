package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Inspiring Overseer reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * SNC's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val InspiringOverseerReprint = Printing(
    oracleId = "d646e42b-5635-4798-b633-29c093b66a55",
    name = "Inspiring Overseer",
    setCode = "FDN",
    collectorNumber = "736",
    artist = "Irina Nordsol",
    imageUri = "https://cards.scryfall.io/normal/front/7/9/79016cf3-6eea-4b21-9ff3-f187d606e19a.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
