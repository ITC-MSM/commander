package com.wingedsheep.mtg.sets.definitions.m21.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Onakke Ogre reprint in M21.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M19's `cards/` package (the card's earliest real printing). This file
 * contributes only the M21-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val OnakkeOgreReprint = Printing(
    oracleId = "fa10cacb-ab14-447a-b411-9d74bc3772fb",
    name = "Onakke Ogre",
    setCode = "M21",
    collectorNumber = "155",
    artist = "Mathias Kollros",
    imageUri = "https://cards.scryfall.io/normal/front/7/6/76e42d07-57d9-4de4-8d41-eb42dd1573ed.jpg?1783930686",
    releaseDate = "2020-07-03",
    rarity = Rarity.COMMON,
)
