package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Mystic Archaeologist reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M19's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val MysticArchaeologistReprint = Printing(
    oracleId = "b77dfe2a-ebc9-46b0-9134-2ecb2abdd8be",
    name = "Mystic Archaeologist",
    setCode = "FDN",
    collectorNumber = "511",
    artist = "Eric Deschamps",
    imageUri = "https://cards.scryfall.io/normal/front/c/0/c069ff32-6759-48a2-8674-4f729c91dcd0.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
