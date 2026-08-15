package com.wingedsheep.mtg.sets.definitions.anb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Loxodon Line Breaker reprint in ANB.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M19's `cards/` package (the card's earliest real printing). This file
 * contributes only the ANB-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val LoxodonLineBreakerReprint = Printing(
    oracleId = "57f0f55d-4cf9-4a35-96dd-05c24c2a9a4f",
    name = "Loxodon Line Breaker",
    setCode = "ANB",
    collectorNumber = "14",
    artist = "Jesper Ejsing",
    imageUri = "https://cards.scryfall.io/normal/front/0/8/087f35cc-ccb4-479a-baf2-8249a58e4a68.jpg?1783929843",
    releaseDate = "2020-08-13",
    rarity = Rarity.COMMON,
)
