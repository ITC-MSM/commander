package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Prosperity reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * VIS's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ProsperityReprint = Printing(
    oracleId = "c586312d-d04a-4bfb-bbb2-b41186ca178e",
    name = "Prosperity",
    setCode = "POR",
    collectorNumber = "66",
    artist = "Phil Foglio",
    imageUri = "https://cards.scryfall.io/normal/front/2/6/269bb4fc-9d8f-42cc-8f71-6a658e41533c.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.RARE,
)
