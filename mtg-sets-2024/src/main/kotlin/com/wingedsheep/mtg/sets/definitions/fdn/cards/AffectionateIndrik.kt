package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Affectionate Indrik reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * GRN's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val AffectionateIndrikReprint = Printing(
    oracleId = "88aa032d-dc11-4cae-81f3-ce66353963e0",
    name = "Affectionate Indrik",
    setCode = "FDN",
    collectorNumber = "211",
    artist = "Steve Prescott",
    imageUri = "https://cards.scryfall.io/normal/front/2/d/2da8347d-06a4-46e0-a55e-cc2da4660263.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
