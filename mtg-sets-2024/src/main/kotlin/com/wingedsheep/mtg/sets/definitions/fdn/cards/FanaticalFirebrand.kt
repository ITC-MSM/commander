package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Fanatical Firebrand reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * RIX's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val FanaticalFirebrandReprint = Printing(
    oracleId = "d36e11f1-6ab3-4273-8114-a8fbbe21c1c3",
    name = "Fanatical Firebrand",
    setCode = "FDN",
    collectorNumber = "195",
    artist = "Wayne Reynolds",
    imageUri = "https://cards.scryfall.io/normal/front/d/1/d1296316-7781-4e98-95e6-7020648be6a5.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
