package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Silvercoat Lion reprint in M11.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M10's `cards/` package (the card's earliest real printing). This file
 * contributes only the M11-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SilvercoatLionReprint = Printing(
    oracleId = "e3c0b11e-7340-4e0a-98b7-91b38439d4f9",
    name = "Silvercoat Lion",
    setCode = "M11",
    collectorNumber = "31",
    artist = "Terese Nielsen",
    imageUri = "https://cards.scryfall.io/normal/front/e/3/e37523d3-7719-48e1-9b3e-2670772a509b.jpg?1783941831",
    releaseDate = "2010-07-16",
    rarity = Rarity.COMMON,
)
