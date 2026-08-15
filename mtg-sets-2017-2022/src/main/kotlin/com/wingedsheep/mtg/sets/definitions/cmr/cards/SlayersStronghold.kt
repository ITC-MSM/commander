package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Slayers' Stronghold reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * AVR's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SlayersStrongholdReprint = Printing(
    oracleId = "2de7367b-a5a4-43f4-8f8f-b931ea28150d",
    name = "Slayers' Stronghold",
    setCode = "CMR",
    collectorNumber = "494",
    artist = "Karl Kopinski",
    imageUri = "https://cards.scryfall.io/normal/front/4/0/402bc1b6-f2f7-4bb7-954a-8579e43f612f.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.RARE,
)
