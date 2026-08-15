package com.wingedsheep.mtg.sets.definitions.s99.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Goblin Cavaliers reprint in S99.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * P02's `cards/` package (the card's earliest real printing). This file
 * contributes only the S99-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GoblinCavaliersReprint = Printing(
    oracleId = "be77e3f8-76a3-4d03-a8be-ad4b0b72f929",
    name = "Goblin Cavaliers",
    setCode = "S99",
    collectorNumber = "98",
    artist = "DiTerlizzi",
    imageUri = "https://cards.scryfall.io/normal/front/c/4/c4d81d6c-b45a-4565-89ef-a6ba20a1e9e7.jpg?1783946030",
    releaseDate = "1999-07-01",
    rarity = Rarity.COMMON,
)
