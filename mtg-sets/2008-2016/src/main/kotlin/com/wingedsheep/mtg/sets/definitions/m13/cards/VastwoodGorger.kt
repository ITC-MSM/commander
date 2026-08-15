package com.wingedsheep.mtg.sets.definitions.m13.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Vastwood Gorger reprint in M13.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * ZEN's `cards/` package (the card's earliest real printing). This file
 * contributes only the M13-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val VastwoodGorgerReprint = Printing(
    oracleId = "a17a86a3-9f9c-4e09-93e6-e543a70733bc",
    name = "Vastwood Gorger",
    setCode = "M13",
    collectorNumber = "196",
    artist = "Kieran Yanner",
    imageUri = "https://cards.scryfall.io/normal/front/7/0/70fc4a5f-1c59-4139-a506-72baebb1168f.jpg?1783940463",
    releaseDate = "2012-07-13",
    rarity = Rarity.COMMON,
)
