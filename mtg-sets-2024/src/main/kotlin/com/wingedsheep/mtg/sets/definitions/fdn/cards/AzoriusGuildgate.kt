package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Azorius Guildgate reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * RTR's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val AzoriusGuildgateReprint = Printing(
    oracleId = "ad1712d8-809f-410c-8b91-ffe6fb8a69a1",
    name = "Azorius Guildgate",
    setCode = "FDN",
    collectorNumber = "683",
    artist = "Drew Baker",
    imageUri = "https://cards.scryfall.io/normal/front/f/9/f98a7264-0a83-42c8-a94d-05ad4c234242.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
