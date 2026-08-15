package com.wingedsheep.mtg.sets.definitions.pz2.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Leopard-Spotted Jiao reprint in PZ2.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * GS1's `cards/` package (the card's earliest real printing). This file
 * contributes only the PZ2-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val LeopardSpottedJiaoReprint = Printing(
    oracleId = "0056e07b-416b-487e-9e6c-3697db402dd4",
    name = "Leopard-Spotted Jiao",
    setCode = "PZ2",
    collectorNumber = "70789",
    artist = "Shinchuen Chen",
    imageUri = "https://cards.scryfall.io/normal/front/c/7/c7761133-59df-4a5e-8c28-deb2ad213986.jpg?1783933910",
    releaseDate = "2018-12-06",
    rarity = Rarity.COMMON,
)
