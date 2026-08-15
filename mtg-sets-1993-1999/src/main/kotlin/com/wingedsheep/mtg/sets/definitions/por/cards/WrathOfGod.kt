package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Wrath of God reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * LEA's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val WrathOfGodReprint = Printing(
    oracleId = "34515b16-c9a4-4f98-8c77-416a7a523407",
    name = "Wrath of God",
    setCode = "POR",
    collectorNumber = "39",
    artist = "Mike Raabe",
    imageUri = "https://cards.scryfall.io/normal/front/d/7/d75d8204-6f9d-4a7a-bb8b-d51ac65a30fa.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.RARE,
)
