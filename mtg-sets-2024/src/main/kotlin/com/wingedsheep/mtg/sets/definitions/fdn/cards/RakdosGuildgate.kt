package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Rakdos Guildgate reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * RTR's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RakdosGuildgateReprint = Printing(
    oracleId = "361f534b-39d1-4421-b5a8-d3813c62f86d",
    name = "Rakdos Guildgate",
    setCode = "FDN",
    collectorNumber = "693",
    artist = "Jonas De Ro",
    imageUri = "https://cards.scryfall.io/normal/front/e/1/e1f01964-c610-4d0f-a2b4-f52e46dc50d2.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
