package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Ambush Viper reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ISD's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val AmbushViperReprint = Printing(
    oracleId = "8957f7c2-040c-4048-9f21-efa7c97682b7",
    name = "Ambush Viper",
    setCode = "CMR",
    collectorNumber = "213",
    artist = "Alan Pollack",
    imageUri = "https://cards.scryfall.io/normal/front/0/e/0e386888-57f5-4eb6-88e8-5679bb8eb290.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
