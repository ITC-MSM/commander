package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Bastion Enforcer reprint in M20.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * AER's `cards/` package (the card's earliest real printing). This file
 * contributes only the M20-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BastionEnforcerReprint = Printing(
    oracleId = "87449f64-9500-4967-a073-cde19824e087",
    name = "Bastion Enforcer",
    setCode = "M20",
    collectorNumber = "303",
    artist = "Matt Stewart",
    imageUri = "https://cards.scryfall.io/normal/front/7/c/7cbf17a0-2dbc-4e79-9cfa-ea49b1605105.jpg?1783932914",
    releaseDate = "2019-07-12",
    rarity = Rarity.COMMON,
)
