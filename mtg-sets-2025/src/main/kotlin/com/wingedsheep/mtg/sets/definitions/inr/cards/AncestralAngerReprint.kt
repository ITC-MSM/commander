package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Ancestral Anger reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * VOW's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val AncestralAngerReprint = Printing(
    oracleId = "e0828e8d-f01f-4088-9123-6d923ddb3242",
    name = "Ancestral Anger",
    setCode = "INR",
    collectorNumber = "141",
    artist = "Randy Vargas",
    imageUri = "https://cards.scryfall.io/normal/front/e/e/eeaca66d-23bd-4e8f-8d4a-c5864dd447be.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.COMMON,
)
