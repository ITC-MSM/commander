package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Pacifism reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val PacifismReprint = Printing(
    oracleId = "5f5e0b10-c8cf-450c-bfd3-bcb0528ec330",
    name = "Pacifism",
    setCode = "FDN",
    collectorNumber = "501",
    scryfallId = "839160d2-44a3-4566-be9d-558d043beac8",
    artist = "Kev Walker",
    imageUri = "https://cards.scryfall.io/normal/front/8/3/839160d2-44a3-4566-be9d-558d043beac8.jpg?1730490501",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
