package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Vastwood Gorger reprint in M12.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * ZEN's `cards/` package (the card's earliest real printing). This file
 * contributes only the M12-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val VastwoodGorgerReprint = Printing(
    oracleId = "a17a86a3-9f9c-4e09-93e6-e543a70733bc",
    name = "Vastwood Gorger",
    setCode = "M12",
    collectorNumber = "200",
    artist = "Kieran Yanner",
    imageUri = "https://cards.scryfall.io/normal/front/c/d/cdd9d448-ebd5-4e01-af88-e755833c2451.jpg?1783941056",
    releaseDate = "2011-07-15",
    rarity = Rarity.COMMON,
)
