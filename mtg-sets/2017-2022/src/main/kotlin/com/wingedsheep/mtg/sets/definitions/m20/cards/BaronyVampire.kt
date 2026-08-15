package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Barony Vampire reprint in M20.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M11's `cards/` package (the card's earliest real printing). This file
 * contributes only the M20-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BaronyVampireReprint = Printing(
    oracleId = "e886756a-298d-4f79-8699-8bfc9345352e",
    name = "Barony Vampire",
    setCode = "M20",
    collectorNumber = "85",
    artist = "Daarken",
    imageUri = "https://cards.scryfall.io/normal/front/b/0/b0130d04-05f2-44f5-bd6c-8b11f798b69e.jpg?1783933000",
    releaseDate = "2019-07-12",
    rarity = Rarity.COMMON,
)
