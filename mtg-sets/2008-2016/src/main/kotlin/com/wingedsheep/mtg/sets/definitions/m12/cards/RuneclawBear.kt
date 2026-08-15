package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Runeclaw Bear reprint in M12.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M10's `cards/` package (the card's earliest real printing). This file
 * contributes only the M12-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RuneclawBearReprint = Printing(
    oracleId = "ec49dfcf-d16d-4621-af4b-4a6f09043221",
    name = "Runeclaw Bear",
    setCode = "M12",
    collectorNumber = "193",
    artist = "Jesper Ejsing",
    imageUri = "https://cards.scryfall.io/normal/front/6/c/6caf2b93-1971-4702-9aa5-bd223eb37a39.jpg?1783941055",
    releaseDate = "2011-07-15",
    rarity = Rarity.COMMON,
)
