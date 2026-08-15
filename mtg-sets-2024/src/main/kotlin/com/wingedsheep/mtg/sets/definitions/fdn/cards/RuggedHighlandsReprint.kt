package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Rugged Highlands reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RuggedHighlandsReprint = Printing(
    oracleId = "6c922206-6e68-4dcd-9559-88da1074f2c4",
    name = "Rugged Highlands",
    setCode = "FDN",
    collectorNumber = "265",
    scryfallId = "fd6eaf8e-8881-4d7b-bafc-75e4ca5cbef6",
    artist = "Eytan Zana",
    imageUri = "https://cards.scryfall.io/normal/front/f/d/fd6eaf8e-8881-4d7b-bafc-75e4ca5cbef6.jpg?1730489590",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
