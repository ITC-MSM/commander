package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Stasis Snare reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * BFZ's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val StasisSnareReprint = Printing(
    oracleId = "6a83768e-672e-4fec-8931-853f5e96d43d",
    name = "Stasis Snare",
    setCode = "FDN",
    collectorNumber = "581",
    artist = "Jason Felix",
    imageUri = "https://cards.scryfall.io/normal/front/c/e/ce41e348-44c2-47f8-8e7d-da2e4d16d648.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
