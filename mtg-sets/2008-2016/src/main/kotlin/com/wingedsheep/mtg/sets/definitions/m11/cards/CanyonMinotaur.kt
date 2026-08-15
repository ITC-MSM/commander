package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Canyon Minotaur reprint in M11.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * CON's `cards/` package (the card's earliest real printing). This file
 * contributes only the M11-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val CanyonMinotaurReprint = Printing(
    oracleId = "0c7163e6-f5a1-45a5-88c2-19dd9ef0a587",
    name = "Canyon Minotaur",
    setCode = "M11",
    collectorNumber = "126",
    artist = "Steve Prescott",
    imageUri = "https://cards.scryfall.io/normal/front/b/6/b670dcc2-e8e3-4fd7-a1db-c3152b005d39.jpg?1783941809",
    releaseDate = "2010-07-16",
    rarity = Rarity.COMMON,
)
