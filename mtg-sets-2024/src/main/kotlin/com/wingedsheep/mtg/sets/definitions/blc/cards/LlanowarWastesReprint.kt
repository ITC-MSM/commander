package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Llanowar Wastes reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val LlanowarWastesReprint = Printing(
    oracleId = "32116127-cf96-4a1b-8896-a1ebc087b597",
    name = "Llanowar Wastes",
    setCode = "BLC",
    collectorNumber = "315",
    scryfallId = "f01e0e0e-f8c1-416d-bf4b-2b7661924e9b",
    artist = "Lucas Graciano",
    imageUri = "https://cards.scryfall.io/normal/front/f/0/f01e0e0e-f8c1-416d-bf4b-2b7661924e9b.jpg?1721429772",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
