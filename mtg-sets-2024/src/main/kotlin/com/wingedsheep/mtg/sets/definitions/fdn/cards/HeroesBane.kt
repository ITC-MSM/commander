package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Heroes' Bane reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * JOU's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val HeroesBaneReprint = Printing(
    oracleId = "bccd722a-b610-4017-93bd-313f08448f5e",
    name = "Heroes' Bane",
    setCode = "FDN",
    collectorNumber = "639",
    artist = "Raymond Swanland",
    imageUri = "https://cards.scryfall.io/normal/front/7/f/7f83195c-5150-4763-a2e1-5b109d185d55.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
