package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Harbinger of the Tides reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ORI's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val HarbingerOfTheTidesReprint = Printing(
    oracleId = "9f5277e8-1cbe-478c-a8bc-ac028f13305e",
    name = "Harbinger of the Tides",
    setCode = "FDN",
    collectorNumber = "593",
    artist = "Svetlin Velinov",
    imageUri = "https://cards.scryfall.io/normal/front/9/c/9ca4f70d-ec17-49a4-8968-598ecdbe8243.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
