package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Canyon Minotaur reprint in M10.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * CON's `cards/` package (the card's earliest real printing). This file
 * contributes only the M10-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val CanyonMinotaurReprint = Printing(
    oracleId = "0c7163e6-f5a1-45a5-88c2-19dd9ef0a587",
    name = "Canyon Minotaur",
    setCode = "M10",
    collectorNumber = "130",
    artist = "Steve Prescott",
    imageUri = "https://cards.scryfall.io/normal/front/b/8/b85b682f-f526-45a8-9a12-db3fe8c3c8c3.jpg?1783942375",
    releaseDate = "2009-07-17",
    rarity = Rarity.COMMON,
)
