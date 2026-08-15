package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Runeclaw Bear reprint in M15.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M10's `cards/` package (the card's earliest real printing). This file
 * contributes only the M15-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RuneclawBearReprint = Printing(
    oracleId = "ec49dfcf-d16d-4621-af4b-4a6f09043221",
    name = "Runeclaw Bear",
    setCode = "M15",
    collectorNumber = "197",
    artist = "Jesper Ejsing",
    imageUri = "https://cards.scryfall.io/normal/front/d/1/d1995238-79cc-4381-9595-71ef11ea1e36.jpg?1783939162",
    releaseDate = "2014-07-18",
    rarity = Rarity.COMMON,
)
