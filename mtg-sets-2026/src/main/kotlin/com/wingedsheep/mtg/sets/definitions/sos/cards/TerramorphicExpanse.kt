package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Terramorphic Expanse reprint in SOS.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * TSP's `cards/` package (the card's earliest real printing). This file contributes only
 * the SOS-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TerramorphicExpanseReprint = Printing(
    oracleId = "1bd3e453-aa21-4ee6-95c2-d6d920ee8e7a",
    name = "Terramorphic Expanse",
    setCode = "SOS",
    collectorNumber = "265",
    artist = "Leon Tukker",
    imageUri = "https://cards.scryfall.io/normal/front/9/a/9a4c5629-fadd-42b9-850f-9f8586a2ca50.jpg",
    releaseDate = "2026-04-24",
    rarity = Rarity.COMMON,
)
