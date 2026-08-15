package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Rampant Growth reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * MIR's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RampantGrowthReprint = Printing(
    oracleId = "8539f295-5d58-4436-a73a-b9277c4c7795",
    name = "Rampant Growth",
    setCode = "CMR",
    collectorNumber = "432",
    artist = "Steven Belledin",
    imageUri = "https://cards.scryfall.io/normal/front/5/c/5cbceb9b-4212-40b4-913f-e4a19db00fae.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
