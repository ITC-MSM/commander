package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Midnight Scavengers reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * EMN's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val MidnightScavengersReprint = Printing(
    oracleId = "1c29ae09-cc93-4a2a-b432-3c78ae01c1ed",
    name = "Midnight Scavengers",
    setCode = "INR",
    collectorNumber = "123",
    artist = "Jason Felix",
    imageUri = "https://cards.scryfall.io/normal/front/b/c/bc079c81-85e8-4a0d-83fe-533c4feaa343.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.COMMON,
)
