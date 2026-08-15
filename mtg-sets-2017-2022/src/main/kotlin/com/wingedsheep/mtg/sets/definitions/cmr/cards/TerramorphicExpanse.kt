package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Terramorphic Expanse reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * TSP's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TerramorphicExpanseReprint = Printing(
    oracleId = "1bd3e453-aa21-4ee6-95c2-d6d920ee8e7a",
    name = "Terramorphic Expanse",
    setCode = "CMR",
    collectorNumber = "357",
    artist = "Dan Murayama Scott",
    imageUri = "https://cards.scryfall.io/normal/front/3/b/3b8b3d63-db84-4aa0-a529-f3c128fae964.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
