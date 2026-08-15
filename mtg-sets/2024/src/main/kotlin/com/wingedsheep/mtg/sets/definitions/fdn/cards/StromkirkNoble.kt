package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Stromkirk Noble reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ISD's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val StromkirkNobleReprint = Printing(
    oracleId = "c55909d3-1f67-4a4a-9d53-8513d6cf96d8",
    name = "Stromkirk Noble",
    setCode = "FDN",
    collectorNumber = "632",
    artist = "James Ryman",
    imageUri = "https://cards.scryfall.io/normal/front/0/f/0f306c7e-cacc-4c26-a3f1-fad4f3ff7cf3.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
