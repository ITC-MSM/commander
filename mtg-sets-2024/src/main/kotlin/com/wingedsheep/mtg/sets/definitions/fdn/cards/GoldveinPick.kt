package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Goldvein Pick reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * KHM's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GoldveinPickReprint = Printing(
    oracleId = "c1624d10-8838-4af8-aea1-a96c0fe6fd6b",
    name = "Goldvein Pick",
    setCode = "FDN",
    collectorNumber = "253",
    artist = "Dan Murayama Scott",
    imageUri = "https://cards.scryfall.io/normal/front/a/2/a241317d-2277-467e-a8f9-aa71c944e244.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
