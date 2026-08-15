package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Thornwood Falls reprint in TDM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the TDM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ThornwoodFallsReprint = Printing(
    oracleId = "ec96cde2-f1e6-495c-94e2-3e8ae79e556c",
    name = "Thornwood Falls",
    setCode = "TDM",
    collectorNumber = "269",
    scryfallId = "ebb502c2-5fd0-46a9-b77d-010f4a942056",
    artist = "Alexander Ostrowski",
    imageUri = "https://cards.scryfall.io/normal/front/e/b/ebb502c2-5fd0-46a9-b77d-010f4a942056.jpg?1743205064",
    releaseDate = "2025-04-11",
    rarity = Rarity.COMMON,
)
