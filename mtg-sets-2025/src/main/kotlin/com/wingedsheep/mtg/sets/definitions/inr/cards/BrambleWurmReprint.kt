package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Bramble Wurm reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * VOW's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BrambleWurmReprint = Printing(
    oracleId = "db4f67a9-6b47-4abe-9367-718da9e0609e",
    name = "Bramble Wurm",
    setCode = "INR",
    collectorNumber = "187",
    artist = "Lars Grant-West",
    imageUri = "https://cards.scryfall.io/normal/front/7/e/7ed00113-a0d7-4825-b066-f911a1ffb900.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.COMMON,
)
