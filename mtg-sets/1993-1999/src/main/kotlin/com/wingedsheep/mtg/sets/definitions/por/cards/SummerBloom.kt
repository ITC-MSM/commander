package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Summer Bloom reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * VIS's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SummerBloomReprint = Printing(
    oracleId = "e5df4597-1647-4ac2-bdb3-a517598d1431",
    name = "Summer Bloom",
    setCode = "POR",
    collectorNumber = "187",
    artist = "Kaja Foglio",
    imageUri = "https://cards.scryfall.io/normal/front/5/e/5e86abcc-272e-4959-90ee-343b9f546ea7.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.RARE,
)
