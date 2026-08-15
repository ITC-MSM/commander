package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Firebrand Archer reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * HOU's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val FirebrandArcherReprint = Printing(
    oracleId = "2e9289d6-dbc6-456d-88cf-d1f534e731d6",
    name = "Firebrand Archer",
    setCode = "FDN",
    collectorNumber = "196",
    artist = "John Stanko",
    imageUri = "https://cards.scryfall.io/normal/front/f/e/fe0312f1-4c98-4b7f-8a34-0059ea80edef.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
