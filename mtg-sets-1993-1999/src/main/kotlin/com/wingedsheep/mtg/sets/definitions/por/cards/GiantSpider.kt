package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Giant Spider reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * LEA's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GiantSpiderReprint = Printing(
    oracleId = "e740ce2f-2134-473c-afa1-1b6d2d1e38ef",
    name = "Giant Spider",
    setCode = "POR",
    collectorNumber = "167",
    artist = "Randy Gallegos",
    imageUri = "https://cards.scryfall.io/normal/front/2/9/2995530c-16bd-4dcb-99c2-008bba00052c.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.COMMON,
)
