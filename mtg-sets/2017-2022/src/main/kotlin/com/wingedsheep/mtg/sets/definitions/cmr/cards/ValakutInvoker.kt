package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Valakut Invoker reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * BFZ's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ValakutInvokerReprint = Printing(
    oracleId = "17ed18cf-33e5-4861-8a36-8f7bb91971f9",
    name = "Valakut Invoker",
    setCode = "CMR",
    collectorNumber = "206",
    artist = "Joseph Meehan",
    imageUri = "https://cards.scryfall.io/normal/front/b/8/b8000d86-60e7-4edd-b685-14ade08b76f2.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
