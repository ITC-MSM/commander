package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Deserted Beach reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * MID's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DesertedBeachReprint = Printing(
    oracleId = "f0ec8681-da50-466b-8cdd-1dc710deccd9",
    name = "Deserted Beach",
    setCode = "INR",
    collectorNumber = "276",
    artist = "Jonas De Ro",
    imageUri = "https://cards.scryfall.io/normal/front/c/8/c819de09-dac2-407a-98c8-775865e9bdf8.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.RARE,
)
