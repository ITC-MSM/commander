package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Hill Giant reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * LEA's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val HillGiantReprint = Printing(
    oracleId = "342199e0-15b6-4824-83da-25caef2592b3",
    name = "Hill Giant",
    setCode = "POR",
    collectorNumber = "133",
    artist = "Randy Gallegos",
    imageUri = "https://cards.scryfall.io/normal/front/7/c/7cd36579-c108-40c0-bce4-38ab837a8c65.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.COMMON,
)
