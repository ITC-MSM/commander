package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Jazal Goldmane reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * C14's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val JazalGoldmaneReprint = Printing(
    oracleId = "609d13dc-c850-4877-8ad3-e84171b58069",
    name = "Jazal Goldmane",
    setCode = "FDN",
    collectorNumber = "497",
    artist = "Aaron Miller",
    imageUri = "https://cards.scryfall.io/normal/front/5/a/5aa8f635-c638-4207-8631-f6b5185f8696.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
