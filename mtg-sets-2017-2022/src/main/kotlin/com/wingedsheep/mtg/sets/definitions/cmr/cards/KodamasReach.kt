package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Kodama's Reach reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * CHK's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val KodamasReachReprint = Printing(
    oracleId = "1593ea18-2f2f-4ab4-83fb-6ccc0bec8a90",
    name = "Kodama's Reach",
    setCode = "CMR",
    collectorNumber = "429",
    artist = "John Avon",
    imageUri = "https://cards.scryfall.io/normal/front/c/3/c36cb953-0553-462c-bd7e-4193987d36c9.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
