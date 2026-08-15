package com.wingedsheep.mtg.sets.definitions.anb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Shrine Keeper reprint in ANB.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * OANA's `cards/` package (the card's earliest real printing). This file
 * contributes only the ANB-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ShrineKeeperReprint = Printing(
    oracleId = "c3319f74-1771-4679-bdad-0ab4b34e9106",
    name = "Shrine Keeper",
    setCode = "ANB",
    collectorNumber = "19",
    artist = "Craig J Spearing",
    imageUri = "https://cards.scryfall.io/normal/front/6/f/6f2d3642-3eda-4b15-96dd-b0f0f9680bd7.jpg?1783929840",
    releaseDate = "2020-08-13",
    rarity = Rarity.COMMON,
)
