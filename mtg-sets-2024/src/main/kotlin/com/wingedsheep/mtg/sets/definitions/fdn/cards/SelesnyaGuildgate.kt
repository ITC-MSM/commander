package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Selesnya Guildgate reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * RTR's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SelesnyaGuildgateReprint = Printing(
    oracleId = "75b235d3-595a-4859-be45-9559d8445db5",
    name = "Selesnya Guildgate",
    setCode = "FDN",
    collectorNumber = "694",
    artist = "Dimitar Marinski",
    imageUri = "https://cards.scryfall.io/normal/front/6/7/6718d4e7-768e-473f-8064-a68422e977f6.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
