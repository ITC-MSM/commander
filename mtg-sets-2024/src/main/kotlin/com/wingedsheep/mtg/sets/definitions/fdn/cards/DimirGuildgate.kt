package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Dimir Guildgate reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * GTC's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DimirGuildgateReprint = Printing(
    oracleId = "52d14717-0cbc-4d7e-b546-54ea91580338",
    name = "Dimir Guildgate",
    setCode = "FDN",
    collectorNumber = "688",
    artist = "Cliff Childs",
    imageUri = "https://cards.scryfall.io/normal/front/f/9/f9b8a159-5e58-4432-8ecd-62f39afa96da.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
