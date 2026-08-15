package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Jalum Tome reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ATQ's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val JalumTomeReprint = Printing(
    oracleId = "32d9fd98-142b-41d2-b8f0-40ae1d4cb991",
    name = "Jalum Tome",
    setCode = "CMR",
    collectorNumber = "318",
    artist = "Jerry Tiritilli",
    imageUri = "https://cards.scryfall.io/normal/front/2/4/24814552-c407-447a-a782-22c69c5b912b.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
