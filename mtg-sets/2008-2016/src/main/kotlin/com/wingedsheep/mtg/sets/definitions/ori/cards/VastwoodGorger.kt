package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Vastwood Gorger reprint in ORI.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * ZEN's `cards/` package (the card's earliest real printing). This file
 * contributes only the ORI-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val VastwoodGorgerReprint = Printing(
    oracleId = "a17a86a3-9f9c-4e09-93e6-e543a70733bc",
    name = "Vastwood Gorger",
    setCode = "ORI",
    collectorNumber = "204",
    artist = "Kieran Yanner",
    imageUri = "https://cards.scryfall.io/normal/front/7/2/72f53dc9-5397-49e1-97d4-3b0b6858f2b2.jpg?1783938318",
    releaseDate = "2015-07-17",
    rarity = Rarity.COMMON,
)
