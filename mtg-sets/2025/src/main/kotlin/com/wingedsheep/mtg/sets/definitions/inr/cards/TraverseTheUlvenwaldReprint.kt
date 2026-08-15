package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Traverse the Ulvenwald reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * SOI's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TraverseTheUlvenwaldReprint = Printing(
    oracleId = "c9dd2bf5-32e0-47f7-9a49-501c262d745b",
    name = "Traverse the Ulvenwald",
    setCode = "INR",
    collectorNumber = "221",
    artist = "Vincent Proce",
    imageUri = "https://cards.scryfall.io/normal/front/7/7/77b459cb-994c-430d-b0a6-59a8dd20adbd.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.RARE,
)
