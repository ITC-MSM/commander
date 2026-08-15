package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Burnished Hart reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * THS's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BurnishedHartReprint = Printing(
    oracleId = "893fed41-c144-433f-af88-bc7d419b7fb3",
    name = "Burnished Hart",
    setCode = "CMR",
    collectorNumber = "302",
    artist = "Yeong-Hao Han",
    imageUri = "https://cards.scryfall.io/normal/front/2/d/2dccb765-8f97-4806-87ef-55415ab09d9b.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
