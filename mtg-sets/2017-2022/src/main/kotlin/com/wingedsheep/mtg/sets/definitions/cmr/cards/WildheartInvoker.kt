package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Wildheart Invoker reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ROE's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val WildheartInvokerReprint = Printing(
    oracleId = "ff8fb796-af5d-4d64-ab3c-7f2469dfa6bf",
    name = "Wildheart Invoker",
    setCode = "CMR",
    collectorNumber = "263",
    artist = "Erica Yang",
    imageUri = "https://cards.scryfall.io/normal/front/1/4/143231b3-23c4-4c6d-8b58-e401e1ac6e29.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
