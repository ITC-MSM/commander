package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Runeclaw Bear reprint in M11.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M10's `cards/` package (the card's earliest real printing). This file
 * contributes only the M11-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RuneclawBearReprint = Printing(
    oracleId = "ec49dfcf-d16d-4621-af4b-4a6f09043221",
    name = "Runeclaw Bear",
    setCode = "M11",
    collectorNumber = "195",
    artist = "Jesper Ejsing",
    imageUri = "https://cards.scryfall.io/normal/front/1/b/1b8ef778-2acf-40f0-9a64-6415d2109093.jpg?1783941793",
    releaseDate = "2010-07-16",
    rarity = Rarity.COMMON,
)
