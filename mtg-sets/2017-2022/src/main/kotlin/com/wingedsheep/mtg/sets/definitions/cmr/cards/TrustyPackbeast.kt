package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Trusty Packbeast reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M19's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TrustyPackbeastReprint = Printing(
    oracleId = "12c7289f-da53-403a-a607-227f43c7e171",
    name = "Trusty Packbeast",
    setCode = "CMR",
    collectorNumber = "53",
    artist = "John Stanko",
    imageUri = "https://cards.scryfall.io/normal/front/0/f/0fcfe1b5-e35b-4a23-8ca8-4dee2ef94f32.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
