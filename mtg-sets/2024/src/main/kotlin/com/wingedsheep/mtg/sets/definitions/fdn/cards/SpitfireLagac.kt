package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Spitfire Lagac reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ZNR's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SpitfireLagacReprint = Printing(
    oracleId = "7215ca87-bbff-4c14-a1f5-de5ddd97c875",
    name = "Spitfire Lagac",
    setCode = "FDN",
    collectorNumber = "208",
    artist = "Antonio José Manzanedo",
    imageUri = "https://cards.scryfall.io/normal/front/3/0/30f600cd-b696-4f49-9cbc-5a33aa43d04c.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
