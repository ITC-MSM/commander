package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Furyblade Vampire reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * EMN's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val FurybladeVampireReprint = Printing(
    oracleId = "f8dbfc60-33e9-4068-a0dd-0d0ed5b9b42c",
    name = "Furyblade Vampire",
    setCode = "INR",
    collectorNumber = "155",
    artist = "Lius Lasahido",
    imageUri = "https://cards.scryfall.io/normal/front/9/5/95a38a53-d83a-4df7-8caa-40995b0c4235.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.UNCOMMON,
)
