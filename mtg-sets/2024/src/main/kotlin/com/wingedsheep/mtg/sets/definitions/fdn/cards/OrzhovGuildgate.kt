package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Orzhov Guildgate reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * GTC's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val OrzhovGuildgateReprint = Printing(
    oracleId = "57b37df5-fee4-4720-931f-f0cb0a8b338c",
    name = "Orzhov Guildgate",
    setCode = "FDN",
    collectorNumber = "692",
    artist = "John Avon",
    imageUri = "https://cards.scryfall.io/normal/front/a/9/a917be03-0c17-4454-b044-c4375e5c8085.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
