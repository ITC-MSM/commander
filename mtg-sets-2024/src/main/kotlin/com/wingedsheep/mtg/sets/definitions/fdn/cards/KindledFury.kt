package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Kindled Fury reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * MOR's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val KindledFuryReprint = Printing(
    oracleId = "ebeee06e-3345-4f02-896f-cb0b2cfa5548",
    name = "Kindled Fury",
    setCode = "FDN",
    collectorNumber = "542",
    artist = "Wayne Reynolds",
    imageUri = "https://cards.scryfall.io/normal/front/a/f/af158462-91e3-4ad7-b435-95d4eb32fe0a.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
