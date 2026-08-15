package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Brazen Scourge reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * KLD's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BrazenScourgeReprint = Printing(
    oracleId = "422ef4ec-863a-450e-9577-a0707bc16532",
    name = "Brazen Scourge",
    setCode = "FDN",
    collectorNumber = "191",
    artist = "Kev Walker",
    imageUri = "https://cards.scryfall.io/normal/front/e/b/eb84b86c-3276-4fc1-a09d-47de388cb729.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
