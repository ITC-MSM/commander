package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Angel of Finality reprint in Kaldheim Commander (KHC).
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Commander 2013's `cards/`
 * package (its earliest real printing). This file contributes only the KHC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val AngelOfFinalityReprint = Printing(
    oracleId = "48d14b5c-711a-4f8a-9de5-55415cb7a79a",
    name = "Angel of Finality",
    setCode = "KHC",
    collectorNumber = "17",
    scryfallId = "fef86cfe-6e4a-4ff7-bb6d-914d8c1e0782",
    artist = "Howard Lyon",
    imageUri = "https://cards.scryfall.io/normal/front/f/e/fef86cfe-6e4a-4ff7-bb6d-914d8c1e0782.jpg?1783928336",
    releaseDate = "2021-02-05",
    rarity = Rarity.RARE,
)
