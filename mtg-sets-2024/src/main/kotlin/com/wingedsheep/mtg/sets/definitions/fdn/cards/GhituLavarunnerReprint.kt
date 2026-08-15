package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Ghitu Lavarunner reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GhituLavarunnerReprint = Printing(
    oracleId = "8e3f4987-6f31-4bc5-8bcd-6adb0440ee7c",
    name = "Ghitu Lavarunner",
    setCode = "FDN",
    collectorNumber = "623",
    scryfallId = "8940d76d-c09f-4d72-a037-439c70ee8d9d",
    artist = "Jesper Ejsing",
    imageUri = "https://cards.scryfall.io/normal/front/8/9/8940d76d-c09f-4d72-a037-439c70ee8d9d.jpg?1730490961",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
