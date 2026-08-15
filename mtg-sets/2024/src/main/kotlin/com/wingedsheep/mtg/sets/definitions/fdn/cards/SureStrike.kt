package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Sure Strike reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * BFZ's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SureStrikeReprint = Printing(
    oracleId = "fb694e7e-f66e-4958-b6ed-aa74bc9ac43e",
    name = "Sure Strike",
    setCode = "FDN",
    collectorNumber = "209",
    artist = "Izzy",
    imageUri = "https://cards.scryfall.io/normal/front/5/d/5de6a1e4-5c66-43e6-9f2a-2635bdab03f6.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
