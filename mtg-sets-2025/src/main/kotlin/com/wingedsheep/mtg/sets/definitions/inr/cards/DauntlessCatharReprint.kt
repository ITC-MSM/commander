package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Dauntless Cathar reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * SOI's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DauntlessCatharReprint = Printing(
    oracleId = "c14bd133-ff17-48b3-a1c8-56c93d8c6e43",
    name = "Dauntless Cathar",
    setCode = "INR",
    collectorNumber = "19",
    artist = "Zack Stella",
    imageUri = "https://cards.scryfall.io/normal/front/b/6/b680d42c-8198-43b0-b934-2add08a02bbc.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.COMMON,
)
