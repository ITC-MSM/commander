package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Spectral Sailor reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M20's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SpectralSailorReprint = Printing(
    oracleId = "a8fdbcdf-479d-4582-9ad5-9fbd4c740c29",
    name = "Spectral Sailor",
    setCode = "FDN",
    collectorNumber = "746",
    artist = "Cristi Balanescu",
    imageUri = "https://cards.scryfall.io/normal/front/5/e/5ef30b6c-0806-4bf6-9a84-c2aea8d84cf1.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
