package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Knight of Grace reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val KnightOfGraceReprint = Printing(
    oracleId = "7b51a979-47e0-4b98-8ec5-8064c83422e1",
    name = "Knight of Grace",
    setCode = "FDN",
    collectorNumber = "576",
    scryfallId = "4efb8633-0a70-4ddc-80af-42508ec75cff",
    artist = "Sidharth Chaturvedi",
    imageUri = "https://cards.scryfall.io/normal/front/4/e/4efb8633-0a70-4ddc-80af-42508ec75cff.jpg?1730490791",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
