package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Yoked Ox reprint in M20.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * THS's `cards/` package (the card's earliest real printing). This file
 * contributes only the M20-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val YokedOxReprint = Printing(
    oracleId = "19b4cf0b-e1e4-4e50-80bc-673bc6ef8bb8",
    name = "Yoked Ox",
    setCode = "M20",
    collectorNumber = "41",
    artist = "Ryan Yee",
    imageUri = "https://cards.scryfall.io/normal/front/a/7/a73f186b-c897-4a98-bc25-8e4aa348d8c9.jpg?1783933018",
    releaseDate = "2019-07-12",
    rarity = Rarity.COMMON,
)
