package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Ironclad Slayer reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * EMN's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val IroncladSlayerReprint = Printing(
    oracleId = "964e20e2-2947-4d8a-a0a2-b8b5fde27ff8",
    name = "Ironclad Slayer",
    setCode = "CMR",
    collectorNumber = "376",
    artist = "Ryan Pancoast",
    imageUri = "https://cards.scryfall.io/normal/front/2/8/2841f538-9686-423f-ac30-0580665112e5.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
