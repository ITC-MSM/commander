package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Overgrown Farmland reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * MID's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val OvergrownFarmlandReprint = Printing(
    oracleId = "709d2f10-1585-48c3-9058-ddd5f62f0452",
    name = "Overgrown Farmland",
    setCode = "INR",
    collectorNumber = "281",
    artist = "Jonas De Ro",
    imageUri = "https://cards.scryfall.io/normal/front/6/6/66656795-a64d-4656-a9bf-050dd9a7b9bf.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.RARE,
)
