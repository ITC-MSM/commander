package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Primal Might reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M21's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val PrimalMightReprint = Printing(
    oracleId = "58d7b8b3-ac47-4805-8259-4ba8e70d7dc3",
    name = "Primal Might",
    setCode = "FDN",
    collectorNumber = "766",
    artist = "Randy Vargas",
    imageUri = "https://cards.scryfall.io/normal/front/6/9/69b3f7b9-9499-4883-b5c5-c5474e470b21.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
