package com.wingedsheep.mtg.sets.definitions.`10e`.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Telling Time reprint in 10E.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types) lives in RAV's
 * `cards/` package (the card's earliest real printing). This file contributes only the
 * 10E-specific presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TellingTimeReprint = Printing(
    oracleId = "192f3c8a-1120-4f7f-ae0f-fe9bde59cd08",
    name = "Telling Time",
    setCode = "10E",
    collectorNumber = "114",
    artist = "Scott M. Fischer",
    imageUri = "https://cards.scryfall.io/normal/front/3/c/3caf69d0-58bd-4cd1-9108-56e6cbf882e4.jpg?1783943047",
    releaseDate = "2007-07-13",
    rarity = Rarity.UNCOMMON,
)
