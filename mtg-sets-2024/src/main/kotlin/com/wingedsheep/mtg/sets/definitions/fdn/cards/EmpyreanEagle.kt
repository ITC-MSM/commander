package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Empyrean Eagle reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M20's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val EmpyreanEagleReprint = Printing(
    oracleId = "270d14b2-07bc-46bc-918f-658102265ccf",
    name = "Empyrean Eagle",
    setCode = "FDN",
    collectorNumber = "239",
    artist = "Jason A. Engle",
    imageUri = "https://cards.scryfall.io/normal/front/5/7/577e99a7-4a55-4314-8f08-2ae0c33b85c7.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
