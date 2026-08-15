package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Circuitous Route reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * GRN's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val CircuitousRouteReprint = Printing(
    oracleId = "30afacd9-4680-4aac-8c22-584f9418822d",
    name = "Circuitous Route",
    setCode = "FDN",
    collectorNumber = "635",
    artist = "Milivoj Ćeran",
    imageUri = "https://cards.scryfall.io/normal/front/a/8/a806dec2-d4f9-4f8d-a1dd-170777941131.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
