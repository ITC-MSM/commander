package com.wingedsheep.mtg.sets.definitions.pz2.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Armored Whirl Turtle reprint in PZ2.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * GS1's `cards/` package (the card's earliest real printing). This file
 * contributes only the PZ2-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ArmoredWhirlTurtleReprint = Printing(
    oracleId = "51a886f2-9b0a-4964-9d59-99dc1a68a97c",
    name = "Armored Whirl Turtle",
    setCode = "PZ2",
    collectorNumber = "70809",
    artist = "Tingting Yeh",
    imageUri = "https://cards.scryfall.io/normal/front/c/a/ca7c3c99-cebf-4b2e-882f-3bd60aaef840.jpg?1783933905",
    releaseDate = "2018-12-06",
    rarity = Rarity.COMMON,
)
