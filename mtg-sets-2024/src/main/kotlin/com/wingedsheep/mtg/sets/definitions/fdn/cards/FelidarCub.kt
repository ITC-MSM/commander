package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Felidar Cub reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * BFZ's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val FelidarCubReprint = Printing(
    oracleId = "cae60cf8-cd64-4595-a4dd-946694cf2bb1",
    name = "Felidar Cub",
    setCode = "FDN",
    collectorNumber = "573",
    artist = "Steve Prescott",
    imageUri = "https://cards.scryfall.io/normal/front/0/d/0d61d50b-7ab6-45a9-b207-31d87aa2e555.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
