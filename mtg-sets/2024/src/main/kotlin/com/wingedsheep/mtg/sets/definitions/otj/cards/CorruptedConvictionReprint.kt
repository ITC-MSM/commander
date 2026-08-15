package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Corrupted Conviction reprint in Outlaws of Thunder Junction.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types) lives in March of the
 * Machine's `cards/` package. This file contributes only the OTJ-specific presentation row —
 * set, collector number, art — picked up automatically by `CardDiscovery.findPrintingsIn`.
 */
val CorruptedConvictionReprint = Printing(
    oracleId = "b45e35df-9032-4482-89a6-c7c50c6d0a79",
    name = "Corrupted Conviction",
    setCode = "OTJ",
    collectorNumber = "84",
    scryfallId = "8046f892-3317-4ef7-9cf7-97b9060540c8",
    artist = "Inkognit",
    imageUri = "https://cards.scryfall.io/normal/front/8/0/8046f892-3317-4ef7-9cf7-97b9060540c8.jpg?1712355575",
    releaseDate = "2024-04-19",
    rarity = Rarity.COMMON,
)
