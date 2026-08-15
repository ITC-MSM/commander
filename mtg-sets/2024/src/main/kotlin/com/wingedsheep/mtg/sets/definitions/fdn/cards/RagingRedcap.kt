package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Raging Redcap reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ELD's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RagingRedcapReprint = Printing(
    oracleId = "679841ad-f3ac-48e2-ba11-2cf6d78642b4",
    name = "Raging Redcap",
    setCode = "FDN",
    collectorNumber = "543",
    artist = "Dan Murayama Scott",
    imageUri = "https://cards.scryfall.io/normal/front/9/6/9627b0a7-bda9-44df-81c9-aa70cc976331.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
