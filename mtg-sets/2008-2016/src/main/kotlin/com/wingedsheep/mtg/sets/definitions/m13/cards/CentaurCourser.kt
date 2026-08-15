package com.wingedsheep.mtg.sets.definitions.m13.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Centaur Courser reprint in M13.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M10's `cards/` package (the card's earliest real printing). This file
 * contributes only the M13-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val CentaurCourserReprint = Printing(
    oracleId = "2f5bf099-2e01-4e1c-9ebf-0ce0ac66939e",
    name = "Centaur Courser",
    setCode = "M13",
    collectorNumber = "164",
    artist = "Vance Kovacs",
    imageUri = "https://cards.scryfall.io/normal/front/4/4/44a5f7db-ea4e-4af5-9d4a-0335db6ea0e9.jpg?1783940475",
    releaseDate = "2012-07-13",
    rarity = Rarity.COMMON,
)
