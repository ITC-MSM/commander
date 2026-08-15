package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Druid of the Cowl reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * AER's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DruidOfTheCowlReprint = Printing(
    oracleId = "b1793c3b-25d6-4fae-a99d-cfdd2210ca67",
    name = "Druid of the Cowl",
    setCode = "FDN",
    collectorNumber = "554",
    artist = "Magali Villeneuve",
    imageUri = "https://cards.scryfall.io/normal/front/d/b/db2d0ee9-865c-4fc9-8cb6-540c597e1bf4.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
