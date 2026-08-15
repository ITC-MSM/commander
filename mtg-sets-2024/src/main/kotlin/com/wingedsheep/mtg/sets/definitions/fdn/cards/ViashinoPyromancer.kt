package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Viashino Pyromancer reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M19's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ViashinoPyromancerReprint = Printing(
    oracleId = "5e78e6e0-60f0-4c3c-b8dd-bd673f5152b8",
    name = "Viashino Pyromancer",
    setCode = "FDN",
    collectorNumber = "634",
    artist = "Jesper Ejsing",
    imageUri = "https://cards.scryfall.io/normal/front/9/f/9f52ada2-cabc-46a3-99df-271833a86909.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
