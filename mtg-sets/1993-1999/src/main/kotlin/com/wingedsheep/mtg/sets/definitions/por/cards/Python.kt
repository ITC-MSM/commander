package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Python reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * VIS's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val PythonReprint = Printing(
    oracleId = "d8a0c3ff-7042-4b52-a216-15e170c8094f",
    name = "Python",
    setCode = "POR",
    collectorNumber = "105",
    artist = "Alan Rabinowitz",
    imageUri = "https://cards.scryfall.io/normal/front/8/2/82c552a1-6245-4caf-8249-765ce7ea80d2.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.COMMON,
)
