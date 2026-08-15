package com.wingedsheep.mtg.sets.definitions.anb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Witch's Familiar reprint in ANB.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * M15's `cards/` package (the card's earliest real printing). This file
 * contributes only the ANB-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val WitchsFamiliarReprint = Printing(
    oracleId = "abe06b4d-e7f0-4125-8cfc-e36296e8bacd",
    name = "Witch's Familiar",
    setCode = "ANB",
    collectorNumber = "66",
    artist = "Jack Wang",
    imageUri = "https://cards.scryfall.io/normal/front/e/1/e1f1f9bd-2891-4338-bcce-c55e55e6c933.jpg?1783929809",
    releaseDate = "2020-08-13",
    rarity = Rarity.COMMON,
)
