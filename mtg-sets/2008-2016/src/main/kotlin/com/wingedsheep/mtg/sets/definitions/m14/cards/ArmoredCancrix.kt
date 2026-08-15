package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Armored Cancrix reprint in M14.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M11's `cards/` package (the card's earliest real printing). This file
 * contributes only the M14-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ArmoredCancrixReprint = Printing(
    oracleId = "445bb343-9758-459b-970f-47f1705f1e55",
    name = "Armored Cancrix",
    setCode = "M14",
    collectorNumber = "44",
    artist = "Tomasz Jedruszek",
    imageUri = "https://cards.scryfall.io/normal/front/3/b/3b455b0f-a69c-43b4-bbf5-605ed41f10e0.jpg?1783939937",
    releaseDate = "2013-07-19",
    rarity = Rarity.COMMON,
)
