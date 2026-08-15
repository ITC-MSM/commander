package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Tragic Slip reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * DKA's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TragicSlipReprint = Printing(
    oracleId = "fa6e2a47-a8cb-4224-ae3e-f61c90477285",
    name = "Tragic Slip",
    setCode = "INR",
    collectorNumber = "134",
    artist = "Christopher Moeller",
    imageUri = "https://cards.scryfall.io/normal/front/1/3/132ccc20-cb42-4474-b747-7e41a661060e.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.COMMON,
)
