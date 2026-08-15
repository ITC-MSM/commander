package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Haunted Ridge reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * MID's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val HauntedRidgeReprint = Printing(
    oracleId = "e2a37967-4212-4553-9f77-bcb613405807",
    name = "Haunted Ridge",
    setCode = "INR",
    collectorNumber = "280",
    artist = "Jonas De Ro",
    imageUri = "https://cards.scryfall.io/normal/front/3/2/32f1e668-89b8-4f82-afc1-6c3efb1fef3b.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.RARE,
)
