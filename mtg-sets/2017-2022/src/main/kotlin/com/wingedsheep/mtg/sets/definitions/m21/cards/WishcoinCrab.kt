package com.wingedsheep.mtg.sets.definitions.m21.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Wishcoin Crab reprint in M21.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * GRN's `cards/` package (the card's earliest real printing). This file
 * contributes only the M21-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val WishcoinCrabReprint = Printing(
    oracleId = "abc519b8-82de-4559-be03-ddb1ddaab993",
    name = "Wishcoin Crab",
    setCode = "M21",
    collectorNumber = "86",
    artist = "James Paick",
    imageUri = "https://cards.scryfall.io/normal/front/3/4/348955a0-e988-48d7-a6a0-a8045fcffd25.jpg?1783930713",
    releaseDate = "2020-07-03",
    rarity = Rarity.COMMON,
)
