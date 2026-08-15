package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Wind-Scarred Crag reprint in TDM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the TDM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val WindScarredCragReprint = Printing(
    oracleId = "b0af0c54-2a59-4075-8543-d41ff20c4c87",
    name = "Wind-Scarred Crag",
    setCode = "TDM",
    collectorNumber = "271",
    scryfallId = "4912e4d0-b16a-4aa6-a583-3430d26bd591",
    artist = "Filip Burburan",
    imageUri = "https://cards.scryfall.io/normal/front/4/9/4912e4d0-b16a-4aa6-a583-3430d26bd591.jpg?1748464664",
    releaseDate = "2025-04-11",
    rarity = Rarity.COMMON,
)
