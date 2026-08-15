package com.wingedsheep.mtg.sets.definitions.dft.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Thornwood Falls reprint in DFT.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the DFT-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ThornwoodFallsReprint = Printing(
    oracleId = "ec96cde2-f1e6-495c-94e2-3e8ae79e556c",
    name = "Thornwood Falls",
    setCode = "DFT",
    collectorNumber = "266",
    scryfallId = "4723a303-7f20-4399-8bc6-6a27a61a3532",
    artist = "Eddie Mendoza",
    imageUri = "https://cards.scryfall.io/normal/front/4/7/4723a303-7f20-4399-8bc6-6a27a61a3532.jpg?1738356957",
    releaseDate = "2025-02-14",
    rarity = Rarity.COMMON,
)
