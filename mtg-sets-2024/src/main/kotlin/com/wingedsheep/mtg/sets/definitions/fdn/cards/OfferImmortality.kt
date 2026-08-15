package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Offer Immortality reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ONE's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val OfferImmortalityReprint = Printing(
    oracleId = "be4499e6-616a-4c11-b37e-9201d2492682",
    name = "Offer Immortality",
    setCode = "FDN",
    collectorNumber = "525",
    artist = "A. M. Sartor",
    imageUri = "https://cards.scryfall.io/normal/front/6/d/6d5802ed-507d-4a52-90e3-d989cd61961b.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
