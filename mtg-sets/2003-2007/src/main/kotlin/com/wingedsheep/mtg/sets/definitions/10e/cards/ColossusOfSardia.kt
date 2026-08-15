package com.wingedsheep.mtg.sets.definitions.`10e`.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Colossus of Sardia reprint in 10E.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ATQ's `cards/` package (the card's earliest real printing). This file contributes only
 * the 10E-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ColossusOfSardiaReprint = Printing(
    oracleId = "9be9625e-b98b-416b-aac4-9f7b2dfbd39d",
    name = "Colossus of Sardia",
    setCode = "10E",
    collectorNumber = "317",
    artist = "Greg Staples",
    imageUri = "https://cards.scryfall.io/normal/front/f/d/fd164011-7a8e-44a2-8599-0a1c0878b5b5.jpg",
    releaseDate = "2007-07-13",
    rarity = Rarity.RARE,
)
