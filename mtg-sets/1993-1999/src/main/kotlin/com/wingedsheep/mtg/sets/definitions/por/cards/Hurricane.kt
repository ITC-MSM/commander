package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Hurricane reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * LEA's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val HurricaneReprint = Printing(
    oracleId = "9c021685-4017-49c7-9f58-2ae0243361a0",
    name = "Hurricane",
    setCode = "POR",
    collectorNumber = "170",
    artist = "Andrew Robinson",
    imageUri = "https://cards.scryfall.io/normal/front/7/b/7b97904e-80ba-4d65-808a-a528200430f8.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.RARE,
)
