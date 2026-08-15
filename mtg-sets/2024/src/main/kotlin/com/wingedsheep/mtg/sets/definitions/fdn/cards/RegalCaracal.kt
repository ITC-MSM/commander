package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Regal Caracal reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * AKH's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RegalCaracalReprint = Printing(
    oracleId = "aa571676-b390-4b8a-baea-4c4cd60c01f6",
    name = "Regal Caracal",
    setCode = "FDN",
    collectorNumber = "579",
    artist = "Filip Burburan",
    imageUri = "https://cards.scryfall.io/normal/front/b/6/b6310a82-73db-40cb-ae64-6f07f869024c.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
