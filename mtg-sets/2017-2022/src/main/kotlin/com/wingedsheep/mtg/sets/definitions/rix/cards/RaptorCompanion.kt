package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Raptor Companion reprint in RIX.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * XLN's `cards/` package (the card's earliest real printing). This file
 * contributes only the RIX-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RaptorCompanionReprint = Printing(
    oracleId = "20e65887-c68c-4dc0-b74b-9ce093f40b06",
    name = "Raptor Companion",
    setCode = "RIX",
    collectorNumber = "19",
    artist = "Slawomir Maniak",
    imageUri = "https://cards.scryfall.io/normal/front/e/1/e16f2ffb-2780-47d2-bcdf-9cef82716b20.jpg?1783935335",
    releaseDate = "2018-01-19",
    rarity = Rarity.COMMON,
)
