package com.wingedsheep.mtg.sets.definitions.m13.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Silvercoat Lion reprint in M13.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M10's `cards/` package (the card's earliest real printing). This file
 * contributes only the M13-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SilvercoatLionReprint = Printing(
    oracleId = "e3c0b11e-7340-4e0a-98b7-91b38439d4f9",
    name = "Silvercoat Lion",
    setCode = "M13",
    collectorNumber = "35",
    artist = "Terese Nielsen",
    imageUri = "https://cards.scryfall.io/normal/front/9/d/9d33e866-cfd8-44e6-8070-df8df1ce965d.jpg?1783940511",
    releaseDate = "2012-07-13",
    rarity = Rarity.COMMON,
)
