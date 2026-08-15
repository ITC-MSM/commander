package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Galestrike reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * AKH's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GalestrikeReprint = Printing(
    oracleId = "3006d992-2714-4264-a4c3-953b67dc7347",
    name = "Galestrike",
    setCode = "CMR",
    collectorNumber = "70",
    artist = "Mike Bierek",
    imageUri = "https://cards.scryfall.io/normal/front/c/2/c23cdb0e-2684-4ad1-b7dd-e355e3e9a221.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
