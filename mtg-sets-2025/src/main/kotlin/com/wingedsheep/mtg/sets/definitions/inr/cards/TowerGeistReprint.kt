package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Tower Geist reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * DKA's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TowerGeistReprint = Printing(
    oracleId = "10e19453-84fb-4845-8db0-c9da52847e09",
    name = "Tower Geist",
    setCode = "INR",
    collectorNumber = "93",
    artist = "Izzy",
    imageUri = "https://cards.scryfall.io/normal/front/8/8/88523d44-4469-4685-8f59-dbcf255e7fe0.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.COMMON,
)
