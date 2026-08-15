package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Burrog Befuddler reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * STX's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BurrogBefuddlerReprint = Printing(
    oracleId = "73b60c2c-7c74-4554-8861-daa0d6fc22b4",
    name = "Burrog Befuddler",
    setCode = "FDN",
    collectorNumber = "504",
    artist = "Zoltan Boros",
    imageUri = "https://cards.scryfall.io/normal/front/c/5/c5f11ea2-cd4c-417a-804c-3df80d9ddd5f.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
