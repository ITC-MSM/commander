package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Burglar Rat reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * GRN's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BurglarRatReprint = Printing(
    oracleId = "2f807301-37df-4724-871a-08e3512b07b3",
    name = "Burglar Rat",
    setCode = "FDN",
    collectorNumber = "170",
    artist = "Tyler Walpole",
    imageUri = "https://cards.scryfall.io/normal/front/d/e/de1c8758-ce3d-49cf-8173-c0eb46f5e7bc.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
