package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Yoked Ox reprint in ORI.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * THS's `cards/` package (the card's earliest real printing). This file
 * contributes only the ORI-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val YokedOxReprint = Printing(
    oracleId = "19b4cf0b-e1e4-4e50-80bc-673bc6ef8bb8",
    name = "Yoked Ox",
    setCode = "ORI",
    collectorNumber = "42",
    artist = "Ryan Yee",
    imageUri = "https://cards.scryfall.io/normal/front/4/3/431069f2-3eba-42f3-b42d-05a7d066b665.jpg?1783938356",
    releaseDate = "2015-07-17",
    rarity = Rarity.COMMON,
)
