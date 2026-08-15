package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Magnigoth Sentry reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * DMU's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val MagnigothSentryReprint = Printing(
    oracleId = "b52824f9-7897-46b7-a4e5-a0261861cb5b",
    name = "Magnigoth Sentry",
    setCode = "FDN",
    collectorNumber = "556",
    artist = "Dave Kendall",
    imageUri = "https://cards.scryfall.io/normal/front/5/6/5624e610-c4c0-4103-a32b-0f1264030a7a.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
