package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Wildsize reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * GPT's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val WildsizeReprint = Printing(
    oracleId = "e1f439c7-217f-4b20-a77d-b9014c5de8c0",
    name = "Wildsize",
    setCode = "CMR",
    collectorNumber = "264",
    artist = "Jim Murray",
    imageUri = "https://cards.scryfall.io/normal/front/e/b/eb0e640d-9a24-471e-8092-0177ec8d824e.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
