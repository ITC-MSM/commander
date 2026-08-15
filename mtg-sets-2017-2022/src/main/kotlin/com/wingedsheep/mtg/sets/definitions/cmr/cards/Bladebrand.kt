package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Bladebrand reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * RNA's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BladebrandReprint = Printing(
    oracleId = "b9cf9f1e-0280-43b6-9d58-262052dcb8a8",
    name = "Bladebrand",
    setCode = "CMR",
    collectorNumber = "110",
    artist = "Winona Nelson",
    imageUri = "https://cards.scryfall.io/normal/front/8/9/89fb59ce-7d40-4784-b96d-2d5a25a8e531.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
