package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Izzet Guildgate reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * RTR's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val IzzetGuildgateReprint = Printing(
    oracleId = "bf75a3d1-f184-4b48-a913-21caee1db084",
    name = "Izzet Guildgate",
    setCode = "FDN",
    collectorNumber = "691",
    artist = "Kirsten Zirngibl",
    imageUri = "https://cards.scryfall.io/normal/front/d/b/db9e6fc9-813d-4a71-8a68-8e0f83fa945d.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
