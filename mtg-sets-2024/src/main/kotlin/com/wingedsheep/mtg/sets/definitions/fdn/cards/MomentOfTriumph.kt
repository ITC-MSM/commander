package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Moment of Triumph reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * RIX's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val MomentOfTriumphReprint = Printing(
    oracleId = "57579799-2582-417f-9864-4560cac39ac3",
    name = "Moment of Triumph",
    setCode = "FDN",
    collectorNumber = "500",
    artist = "Steven Belledin",
    imageUri = "https://cards.scryfall.io/normal/front/9/1/911deea0-38d2-41d9-a2ae-fb8eb45b2c23.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
