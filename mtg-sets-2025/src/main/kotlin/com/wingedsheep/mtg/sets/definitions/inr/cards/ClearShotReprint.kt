package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Clear Shot reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * EMN's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ClearShotReprint = Printing(
    oracleId = "0b8defd2-8530-47d7-a0c6-27aa5162dff6",
    name = "Clear Shot",
    setCode = "INR",
    collectorNumber = "188",
    artist = "Craig J Spearing",
    imageUri = "https://cards.scryfall.io/normal/front/8/8/88b8905b-68d3-491b-b7fe-eed74841e9a0.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.UNCOMMON,
)
