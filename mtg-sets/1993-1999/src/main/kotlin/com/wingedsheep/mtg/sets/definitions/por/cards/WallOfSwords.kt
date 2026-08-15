package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Wall of Swords reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * LEA's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val WallOfSwordsReprint = Printing(
    oracleId = "eb098958-50d3-4476-ba74-382033703ff9",
    name = "Wall of Swords",
    setCode = "POR",
    collectorNumber = "37",
    artist = "Douglas Shuler",
    imageUri = "https://cards.scryfall.io/normal/front/3/e/3e8d55a3-0d7f-4fba-9879-9a8264110e78.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.UNCOMMON,
)
