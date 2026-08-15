package com.wingedsheep.mtg.sets.definitions.s99.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Goblin Hero reprint in S99.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * DRK's `cards/` package (the card's earliest real printing). This file
 * contributes only the S99-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GoblinHeroReprint = Printing(
    oracleId = "ee969637-a20e-4163-97c0-9fd5cb17b741",
    name = "Goblin Hero",
    setCode = "S99",
    collectorNumber = "103",
    artist = "Pete Venters",
    imageUri = "https://cards.scryfall.io/normal/front/c/3/c3ed9cd3-5e6a-4e86-b120-ff27b744311d.jpg?1783946031",
    releaseDate = "1999-07-01",
    rarity = Rarity.COMMON,
)
