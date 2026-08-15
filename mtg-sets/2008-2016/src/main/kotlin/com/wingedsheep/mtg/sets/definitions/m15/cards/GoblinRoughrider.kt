package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Goblin Roughrider reprint in M15.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * WWK's `cards/` package (the card's earliest real printing). This file
 * contributes only the M15-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GoblinRoughriderReprint = Printing(
    oracleId = "a920d37e-e172-41f7-bfbf-c18821dcbed7",
    name = "Goblin Roughrider",
    setCode = "M15",
    collectorNumber = "146",
    artist = "Jesper Ejsing",
    imageUri = "https://cards.scryfall.io/normal/front/9/0/9097ec4a-6c0e-4c27-8910-29ac47612031.jpg?1783939173",
    releaseDate = "2014-07-18",
    rarity = Rarity.COMMON,
)
