package com.wingedsheep.mtg.sets.definitions.pz2.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Flood reprint in PZ2 (Treasure Chest).
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in DRK's `cards/` package
 * (the card's earliest real printing). This file contributes only the PZ2-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val FloodReprint = Printing(
    oracleId = "e8eb2abb-daf9-43e0-b909-d96b679f71c2",
    name = "Flood",
    setCode = "PZ2",
    collectorNumber = "65829",
    scryfallId = "941de0f9-7500-45e5-a6aa-40b7531a2b25",
    artist = "Jonas De Ro",
    imageUri = "https://cards.scryfall.io/normal/front/9/4/941de0f9-7500-45e5-a6aa-40b7531a2b25.jpg?1783936975",
    releaseDate = "2016-11-16",
    rarity = Rarity.COMMON,
)
