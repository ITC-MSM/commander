package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Devout Decree reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in the M20 (Core Set 2020)
 * `cards/` package — its earliest real printing. This file contributes only the FDN
 * presentation row, picked up automatically by `CardDiscovery.findPrintingsIn`.
 */
val DevoutDecreeReprint = Printing(
    oracleId = "92f8c8b1-1fd5-4e80-b8a9-51671937aaf2",
    name = "Devout Decree",
    setCode = "FDN",
    collectorNumber = "571",
    scryfallId = "d174cb01-b1cf-444c-a893-cc61ddf88b8c",
    artist = "Zoltan Boros",
    imageUri = "https://cards.scryfall.io/normal/front/d/1/d174cb01-b1cf-444c-a893-cc61ddf88b8c.jpg?1782688768",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
