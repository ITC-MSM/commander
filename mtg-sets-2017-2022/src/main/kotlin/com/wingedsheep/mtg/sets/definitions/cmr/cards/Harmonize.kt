package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Harmonize reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * PLC's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val HarmonizeReprint = Printing(
    oracleId = "7eff84f1-f772-497a-b350-bbc93d0230f7",
    name = "Harmonize",
    setCode = "CMR",
    collectorNumber = "427",
    artist = "Paul Lee",
    imageUri = "https://cards.scryfall.io/normal/front/1/5/159e0372-de5e-4830-b690-f5154ed1036c.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
