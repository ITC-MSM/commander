package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Centaur Courser reprint in M20.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M10's `cards/` package (the card's earliest real printing). This file
 * contributes only the M20-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val CentaurCourserReprint = Printing(
    oracleId = "2f5bf099-2e01-4e1c-9ebf-0ce0ac66939e",
    name = "Centaur Courser",
    setCode = "M20",
    collectorNumber = "168",
    artist = "Vance Kovacs",
    imageUri = "https://cards.scryfall.io/normal/front/e/8/e8b67ee8-3189-4426-8b1a-b540267768fd.jpg?1783932966",
    releaseDate = "2019-07-12",
    rarity = Rarity.COMMON,
)
