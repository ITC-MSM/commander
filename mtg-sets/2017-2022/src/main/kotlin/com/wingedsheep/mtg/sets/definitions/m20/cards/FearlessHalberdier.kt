package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Fearless Halberdier reprint in M20.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * GRN's `cards/` package (the card's earliest real printing). This file
 * contributes only the M20-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val FearlessHalberdierReprint = Printing(
    oracleId = "2ef2f417-2061-4796-974a-92f800ef05e4",
    name = "Fearless Halberdier",
    setCode = "M20",
    collectorNumber = "329",
    artist = "Suzanne Helmigh",
    imageUri = "https://cards.scryfall.io/normal/front/8/9/89f08297-f477-4330-a99e-3f0847c31364.jpg?1783932904",
    releaseDate = "2019-07-12",
    rarity = Rarity.COMMON,
)
