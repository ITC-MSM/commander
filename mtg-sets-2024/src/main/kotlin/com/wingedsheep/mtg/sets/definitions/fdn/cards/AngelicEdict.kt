package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Angelic Edict reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * GTC's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val AngelicEdictReprint = Printing(
    oracleId = "36de8401-ee4e-413f-91ad-06924e39c857",
    name = "Angelic Edict",
    setCode = "FDN",
    collectorNumber = "490",
    artist = "Trevor Claxton",
    imageUri = "https://cards.scryfall.io/normal/front/c/0/c0bf349e-2974-4aea-a5c0-fdaea77325cc.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
