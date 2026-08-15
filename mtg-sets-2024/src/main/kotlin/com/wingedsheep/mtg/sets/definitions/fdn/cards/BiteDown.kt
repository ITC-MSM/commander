package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Bite Down reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * DMU's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BiteDownReprint = Printing(
    oracleId = "623903de-3c04-4745-9af3-d7ec9fb2574d",
    name = "Bite Down",
    setCode = "FDN",
    collectorNumber = "212",
    artist = "Kitt Lapeña",
    imageUri = "https://cards.scryfall.io/normal/front/f/8/f8d70b3b-f6f9-4b3c-ad70-0ce369e812b5.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
