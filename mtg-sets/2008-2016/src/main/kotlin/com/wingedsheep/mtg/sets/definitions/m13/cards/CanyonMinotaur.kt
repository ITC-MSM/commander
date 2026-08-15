package com.wingedsheep.mtg.sets.definitions.m13.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Canyon Minotaur reprint in M13.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * CON's `cards/` package (the card's earliest real printing). This file
 * contributes only the M13-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val CanyonMinotaurReprint = Printing(
    oracleId = "0c7163e6-f5a1-45a5-88c2-19dd9ef0a587",
    name = "Canyon Minotaur",
    setCode = "M13",
    collectorNumber = "122",
    artist = "Steve Prescott",
    imageUri = "https://cards.scryfall.io/normal/front/f/8/f8dc0efb-5847-4061-b386-9b4099361a58.jpg?1783940487",
    releaseDate = "2012-07-13",
    rarity = Rarity.COMMON,
)
