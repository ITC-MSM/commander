package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Simic Guildgate reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * GTC's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SimicGuildgateReprint = Printing(
    oracleId = "e8705df9-6439-4930-91b6-229f818559af",
    name = "Simic Guildgate",
    setCode = "FDN",
    collectorNumber = "695",
    artist = "Svetlin Velinov",
    imageUri = "https://cards.scryfall.io/normal/front/9/6/96590855-1ee5-4d69-9070-776e23f71976.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
