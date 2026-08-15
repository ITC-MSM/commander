package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Guttersnipe reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * RTR's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GuttersnipeReprint = Printing(
    oracleId = "c6bdaf76-6a03-4695-9c4b-f040e73435af",
    name = "Guttersnipe",
    setCode = "FDN",
    collectorNumber = "716",
    artist = "Andrey Kuzinskiy",
    imageUri = "https://cards.scryfall.io/normal/front/d/d/dde674d6-f4e7-410b-acf0-34021290576d.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
