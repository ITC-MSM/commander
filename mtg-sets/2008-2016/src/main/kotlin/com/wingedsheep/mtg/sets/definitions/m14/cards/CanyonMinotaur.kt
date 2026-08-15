package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Canyon Minotaur reprint in M14.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * CON's `cards/` package (the card's earliest real printing). This file
 * contributes only the M14-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val CanyonMinotaurReprint = Printing(
    oracleId = "0c7163e6-f5a1-45a5-88c2-19dd9ef0a587",
    name = "Canyon Minotaur",
    setCode = "M14",
    collectorNumber = "131",
    artist = "Steve Prescott",
    imageUri = "https://cards.scryfall.io/normal/front/3/4/3469d73e-6de1-4b91-83e3-b1714ac29268.jpg?1783939915",
    releaseDate = "2013-07-19",
    rarity = Rarity.COMMON,
)
