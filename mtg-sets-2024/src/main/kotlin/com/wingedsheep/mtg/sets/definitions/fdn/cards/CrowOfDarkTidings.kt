package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Crow of Dark Tidings reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * SOI's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val CrowOfDarkTidingsReprint = Printing(
    oracleId = "a4ffe297-5e82-43f9-91a4-7aa3d8dd3b4a",
    name = "Crow of Dark Tidings",
    setCode = "FDN",
    collectorNumber = "519",
    artist = "Simon Dominic",
    imageUri = "https://cards.scryfall.io/normal/front/2/c/2cd74e93-064a-42d7-8e6e-c413912a08cd.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
