package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Prowling Caracal reprint in M20.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * RNA's `cards/` package (the card's earliest real printing). This file
 * contributes only the M20-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ProwlingCaracalReprint = Printing(
    oracleId = "0eabe5e9-cb03-4481-9360-5e79eccd0631",
    name = "Prowling Caracal",
    setCode = "M20",
    collectorNumber = "309",
    artist = "Jonathan Kuo",
    imageUri = "https://cards.scryfall.io/normal/front/1/e/1e689e4a-fc54-46f4-b0c5-c0e65d88340e.jpg?1783932912",
    releaseDate = "2019-07-12",
    rarity = Rarity.COMMON,
)
