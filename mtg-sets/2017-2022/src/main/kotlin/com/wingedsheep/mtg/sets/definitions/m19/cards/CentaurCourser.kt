package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Centaur Courser reprint in M19.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M10's `cards/` package (the card's earliest real printing). This file
 * contributes only the M19-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val CentaurCourserReprint = Printing(
    oracleId = "2f5bf099-2e01-4e1c-9ebf-0ce0ac66939e",
    name = "Centaur Courser",
    setCode = "M19",
    collectorNumber = "171",
    artist = "Vance Kovacs",
    imageUri = "https://cards.scryfall.io/normal/front/b/a/bae6eb55-4bf9-4418-b667-a9a761f91ef9.jpg?1783934541",
    releaseDate = "2018-07-13",
    rarity = Rarity.COMMON,
)
