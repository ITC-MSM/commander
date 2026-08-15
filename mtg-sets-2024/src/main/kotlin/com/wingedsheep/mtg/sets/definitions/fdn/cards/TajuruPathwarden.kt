package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Tajuru Pathwarden reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * OGW's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TajuruPathwardenReprint = Printing(
    oracleId = "d8c7cd66-d8bf-4a99-add5-1d5651d4e025",
    name = "Tajuru Pathwarden",
    setCode = "FDN",
    collectorNumber = "558",
    artist = "Victor Adame Minguez",
    imageUri = "https://cards.scryfall.io/normal/front/d/a/da20a0d3-2022-4dea-84c8-85adc5a974f8.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
