package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Blossoming Sands reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BlossomingSandsReprint = Printing(
    oracleId = "45429b2c-be3b-4b2e-9bab-a059ccbda8cd",
    name = "Blossoming Sands",
    setCode = "FDN",
    collectorNumber = "260",
    scryfallId = "37676ed8-588c-4bca-8065-874b74d84807",
    artist = "Sam Burley",
    imageUri = "https://cards.scryfall.io/normal/front/3/7/37676ed8-588c-4bca-8065-874b74d84807.jpg?1730489573",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
