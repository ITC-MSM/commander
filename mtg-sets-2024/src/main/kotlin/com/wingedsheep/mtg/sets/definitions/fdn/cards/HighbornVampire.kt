package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Highborn Vampire reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ZNR's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val HighbornVampireReprint = Printing(
    oracleId = "1adcf587-9035-4299-a2bd-62f805fdd897",
    name = "Highborn Vampire",
    setCode = "FDN",
    collectorNumber = "522",
    artist = "Denman Rooke",
    imageUri = "https://cards.scryfall.io/normal/front/5/3/53c56a23-eb4f-4be1-ada9-1ad5b80195d3.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
