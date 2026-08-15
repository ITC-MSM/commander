package com.wingedsheep.mtg.sets.definitions.dft.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Bloodfell Caves reprint in DFT.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the DFT-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BloodfellCavesReprint = Printing(
    oracleId = "64e29bfc-9313-4e8c-808c-bc27f6b018a6",
    name = "Bloodfell Caves",
    setCode = "DFT",
    collectorNumber = "251",
    scryfallId = "49195d90-de0c-4290-aa1c-9f4d948b5521",
    artist = "Ron Spencer",
    imageUri = "https://cards.scryfall.io/normal/front/4/9/49195d90-de0c-4290-aa1c-9f4d948b5521.jpg?1738356883",
    releaseDate = "2025-02-14",
    rarity = Rarity.COMMON,
)
