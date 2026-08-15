package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Volcanic Dragon reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * MIR's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val VolcanicDragonReprint = Printing(
    oracleId = "994db177-03f5-43dd-bf7b-2994e8d430d3",
    name = "Volcanic Dragon",
    setCode = "CMR",
    collectorNumber = "207",
    artist = "Chris Rahn",
    imageUri = "https://cards.scryfall.io/normal/front/4/6/46419d29-21a1-4753-a2f0-1d0d996ec54e.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
