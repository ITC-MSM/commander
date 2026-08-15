package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Cultivator's Caravan reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * KLD's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val CultivatorsCaravanReprint = Printing(
    oracleId = "c1eb530c-dd36-40ae-8617-6bb6969565e1",
    name = "Cultivator's Caravan",
    setCode = "FDN",
    collectorNumber = "670",
    artist = "Mark Zug",
    imageUri = "https://cards.scryfall.io/normal/front/2/4/249aa94c-85ab-4606-aa2c-c902bd83ac21.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
