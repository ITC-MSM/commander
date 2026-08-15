package com.wingedsheep.mtg.sets.definitions.anb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Rumbling Baloth reprint in ANB.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M14's `cards/` package (the card's earliest real printing). This file
 * contributes only the ANB-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RumblingBalothReprint = Printing(
    oracleId = "3191c6ca-4d25-4ba3-bfe1-4aeab1295573",
    name = "Rumbling Baloth",
    setCode = "ANB",
    collectorNumber = "103",
    artist = "Jesper Ejsing",
    imageUri = "https://cards.scryfall.io/normal/front/c/2/c28b7565-3da0-4878-bf5d-188f7019d47f.jpg?1783929787",
    releaseDate = "2020-08-13",
    rarity = Rarity.COMMON,
)
