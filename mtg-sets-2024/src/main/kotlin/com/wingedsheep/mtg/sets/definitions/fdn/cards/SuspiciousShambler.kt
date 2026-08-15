package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Suspicious Shambler reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * J22's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SuspiciousShamblerReprint = Printing(
    oracleId = "ea6ee33f-0cba-4e96-817f-dc89c9064ead",
    name = "Suspicious Shambler",
    setCode = "FDN",
    collectorNumber = "527",
    artist = "Javier Charro",
    imageUri = "https://cards.scryfall.io/normal/front/3/d/3d2c5345-eb55-4bca-9183-b4e1404405f8.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
