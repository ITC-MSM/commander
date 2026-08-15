package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Axgard Cavalry reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * KHM's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val AxgardCavalryReprint = Printing(
    oracleId = "6a92b011-fd01-4b6a-a17c-73ac1967ff98",
    name = "Axgard Cavalry",
    setCode = "FDN",
    collectorNumber = "189",
    artist = "Evyn Fong",
    imageUri = "https://cards.scryfall.io/normal/front/f/e/fe3cc41a-adae-4c9b-b4d3-03f3ca862fed.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
