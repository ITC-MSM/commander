package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Ancestral Memories reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * MIR's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val AncestralMemoriesReprint = Printing(
    oracleId = "95a2802a-2621-40c3-84f8-51e8aad7b6f0",
    name = "Ancestral Memories",
    setCode = "POR",
    collectorNumber = "40",
    artist = "Dan Frazier",
    imageUri = "https://cards.scryfall.io/normal/front/c/f/cf9b613c-61bf-4c2d-9c90-2949e442aea5.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.RARE,
)
