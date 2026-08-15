package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Sailor of Means reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * XLN's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SailorOfMeansReprint = Printing(
    oracleId = "c4efae03-99de-4439-925b-504602623bfd",
    name = "Sailor of Means",
    setCode = "CMR",
    collectorNumber = "88",
    artist = "Ryan Pancoast",
    imageUri = "https://cards.scryfall.io/normal/front/2/1/212e9011-a9b7-4a60-bb47-ce5c3c147280.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
