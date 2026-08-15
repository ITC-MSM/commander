package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Shipwreck Marsh reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * MID's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ShipwreckMarshReprint = Printing(
    oracleId = "5f42b67f-87fd-4f98-a0e8-0c8313f4bbc8",
    name = "Shipwreck Marsh",
    setCode = "INR",
    collectorNumber = "284",
    artist = "Jonas De Ro",
    imageUri = "https://cards.scryfall.io/normal/front/1/5/156df6eb-1ac9-4954-bf93-b1668096b8bd.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.RARE,
)
