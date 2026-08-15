package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Blood Mist reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * EMN's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BloodMistReprint = Printing(
    oracleId = "22188333-69b6-4685-b96b-b8a102ad5531",
    name = "Blood Mist",
    setCode = "INR",
    collectorNumber = "143",
    artist = "Joseph Meehan",
    imageUri = "https://cards.scryfall.io/normal/front/2/c/2c01ef93-f41a-406f-91fc-f015b6014575.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.UNCOMMON,
)
