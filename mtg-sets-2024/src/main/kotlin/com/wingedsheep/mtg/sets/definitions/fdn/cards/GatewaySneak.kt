package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Gateway Sneak reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * RNA's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GatewaySneakReprint = Printing(
    oracleId = "66c227f2-0e74-43e2-ab24-3866d15c5eef",
    name = "Gateway Sneak",
    setCode = "FDN",
    collectorNumber = "592",
    artist = "Matt Stewart",
    imageUri = "https://cards.scryfall.io/normal/front/3/8/3834176f-29c5-4511-87d6-75d0c348a770.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
