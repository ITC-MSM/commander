package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Pyroclasm reprint in DSK.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the DSK-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val PyroclasmReprint = Printing(
    oracleId = "e4bcd4ea-e7cd-4471-8f3b-18bb51d3d70c",
    name = "Pyroclasm",
    setCode = "DSK",
    collectorNumber = "149",
    scryfallId = "4391b0af-2f26-4a45-9e2a-5bd8e9838107",
    artist = "Néstor Ossandón Leal",
    imageUri = "https://cards.scryfall.io/normal/front/4/3/4391b0af-2f26-4a45-9e2a-5bd8e9838107.jpg?1726286406",
    releaseDate = "2024-09-27",
    rarity = Rarity.UNCOMMON,
)
