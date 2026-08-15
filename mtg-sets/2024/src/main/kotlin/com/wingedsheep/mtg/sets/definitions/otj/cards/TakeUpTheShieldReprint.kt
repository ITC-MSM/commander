package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Take Up the Shield reprint in OTJ.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types) lives in the
 * Dominaria United `cards/` package (earliest real-expansion printing). This file contributes
 * only the OTJ-specific presentation row — set, collector number, art — picked up
 * automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TakeUpTheShieldReprint = Printing(
    oracleId = "e8d0eafe-540a-4b2e-a989-e98ff3c31105",
    name = "Take Up the Shield",
    setCode = "OTJ",
    collectorNumber = "34",
    scryfallId = "76a31968-ba6d-4c01-838f-4cb8c64e73fb",
    artist = "Josiah \"Jo\" Cameron",
    imageUri = "https://cards.scryfall.io/normal/front/7/6/76a31968-ba6d-4c01-838f-4cb8c64e73fb.jpg?1712355363",
    releaseDate = "2024-04-19",
    rarity = Rarity.COMMON,
)
