package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Nessian Hornbeetle reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * THB's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val NessianHornbeetleReprint = Printing(
    oracleId = "ab097f46-a7bf-4cf1-a96c-36b681fe79da",
    name = "Nessian Hornbeetle",
    setCode = "FDN",
    collectorNumber = "229",
    artist = "Jason Felix",
    imageUri = "https://cards.scryfall.io/normal/front/3/d/3d4d93de-85c6-4653-8ddd-d8bf21516d44.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
