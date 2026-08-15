package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Vorstclaw reprint in M20.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * AVR's `cards/` package (the card's earliest real printing). This file
 * contributes only the M20-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val VorstclawReprint = Printing(
    oracleId = "aa944cbe-fdba-4001-97d5-c722fe744dcc",
    name = "Vorstclaw",
    setCode = "M20",
    collectorNumber = "201",
    artist = "Lucas Graciano",
    imageUri = "https://cards.scryfall.io/normal/front/7/9/79719ed0-468d-4946-8dfc-fb7e2b2e305e.jpg?1783932954",
    releaseDate = "2019-07-12",
    rarity = Rarity.COMMON,
)
