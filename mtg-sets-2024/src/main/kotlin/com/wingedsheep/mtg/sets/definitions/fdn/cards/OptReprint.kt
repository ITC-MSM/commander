package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Opt reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val OptReprint = Printing(
    oracleId = "713332c1-5bd8-400f-bfff-c1ca0697a043",
    name = "Opt",
    setCode = "FDN",
    collectorNumber = "512",
    scryfallId = "58d26b54-0093-4e90-a2b1-b57c64340f9c",
    artist = "Tyler Jacobson",
    imageUri = "https://cards.scryfall.io/normal/front/5/8/58d26b54-0093-4e90-a2b1-b57c64340f9c.jpg?1730490543",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
