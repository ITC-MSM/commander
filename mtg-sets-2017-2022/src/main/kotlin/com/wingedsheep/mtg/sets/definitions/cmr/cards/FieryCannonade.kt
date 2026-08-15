package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Fiery Cannonade reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * XLN's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val FieryCannonadeReprint = Printing(
    oracleId = "7e108b31-2ea3-4129-b324-a2d31c78bbe6",
    name = "Fiery Cannonade",
    setCode = "CMR",
    collectorNumber = "178",
    artist = "Ben Wootten",
    imageUri = "https://cards.scryfall.io/normal/front/3/9/396f1cdf-712b-4518-a0e8-0039303dccdc.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
