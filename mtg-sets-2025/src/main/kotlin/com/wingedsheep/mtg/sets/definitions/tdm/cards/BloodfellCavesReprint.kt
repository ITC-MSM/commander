package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Bloodfell Caves reprint in TDM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the TDM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BloodfellCavesReprint = Printing(
    oracleId = "64e29bfc-9313-4e8c-808c-bc27f6b018a6",
    name = "Bloodfell Caves",
    setCode = "TDM",
    collectorNumber = "250",
    scryfallId = "1dde3c68-6f29-4c00-b668-c25ac9e3e13b",
    artist = "Piotr Dura",
    imageUri = "https://cards.scryfall.io/normal/front/1/d/1dde3c68-6f29-4c00-b668-c25ac9e3e13b.jpg?1743204990",
    releaseDate = "2025-04-11",
    rarity = Rarity.COMMON,
)
