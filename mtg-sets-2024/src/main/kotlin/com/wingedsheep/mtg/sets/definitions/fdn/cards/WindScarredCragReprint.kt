package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Wind-Scarred Crag reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val WindScarredCragReprint = Printing(
    oracleId = "b0af0c54-2a59-4075-8543-d41ff20c4c87",
    name = "Wind-Scarred Crag",
    setCode = "FDN",
    collectorNumber = "271",
    scryfallId = "759e99df-11a8-4aee-b6bc-344e84e10d94",
    artist = "Jonas De Ro",
    imageUri = "https://cards.scryfall.io/normal/front/7/5/759e99df-11a8-4aee-b6bc-344e84e10d94.jpg?1730489611",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
