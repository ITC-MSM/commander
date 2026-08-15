package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Seraph of Dawn reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * AVR's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SeraphOfDawnReprint = Printing(
    oracleId = "38c588ae-7254-4fae-aa9a-03e2a5524492",
    name = "Seraph of Dawn",
    setCode = "CMR",
    collectorNumber = "44",
    artist = "Todd Lockwood",
    imageUri = "https://cards.scryfall.io/normal/front/6/4/64bf33ea-2d2d-476d-ab1d-fba204fd034b.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
