package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Dive Down reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * XLN's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DiveDownReprint = Printing(
    oracleId = "2f990b54-fbf3-4949-85bb-9ba39710e72a",
    name = "Dive Down",
    setCode = "FDN",
    collectorNumber = "588",
    artist = "Magali Villeneuve",
    imageUri = "https://cards.scryfall.io/normal/front/6/5/65d5cff9-a3ec-432d-9ce5-68949e524279.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
