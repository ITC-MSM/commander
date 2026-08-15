package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Youthful Valkyrie reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * KHM's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val YouthfulValkyrieReprint = Printing(
    oracleId = "6d37ba4b-ff56-4eec-9dc2-2d7f357dc9c9",
    name = "Youthful Valkyrie",
    setCode = "FDN",
    collectorNumber = "149",
    artist = "Anna Steinbauer",
    imageUri = "https://cards.scryfall.io/normal/front/9/d/9d795f79-c3a5-4ea1-a5cf-1ce73d6837b6.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
