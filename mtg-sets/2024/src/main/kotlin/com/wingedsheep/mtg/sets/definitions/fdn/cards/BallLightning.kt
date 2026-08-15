package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Ball Lightning reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * DRK's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BallLightningReprint = Printing(
    oracleId = "7485cf25-eb41-4397-be13-7f0b8c10c70a",
    name = "Ball Lightning",
    setCode = "FDN",
    collectorNumber = "618",
    artist = "Trevor Claxton",
    imageUri = "https://cards.scryfall.io/normal/front/5/f/5f27dbf0-6818-40ea-832d-10686b4c2900.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
