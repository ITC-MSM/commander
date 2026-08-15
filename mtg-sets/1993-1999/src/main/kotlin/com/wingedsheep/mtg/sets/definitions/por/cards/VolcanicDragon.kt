package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Volcanic Dragon reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * MIR's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val VolcanicDragonReprint = Printing(
    oracleId = "994db177-03f5-43dd-bf7b-2994e8d430d3",
    name = "Volcanic Dragon",
    setCode = "POR",
    collectorNumber = "153",
    artist = "Tom Wänerstrand",
    imageUri = "https://cards.scryfall.io/normal/front/d/9/d99c5c70-7568-42d3-939c-b6ee1ed94b9f.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.RARE,
)
