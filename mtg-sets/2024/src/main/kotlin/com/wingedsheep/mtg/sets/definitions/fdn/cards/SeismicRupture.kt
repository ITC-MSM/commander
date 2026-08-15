package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Seismic Rupture reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * DTK's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SeismicRuptureReprint = Printing(
    oracleId = "3b1f1a57-d83a-443f-b190-03df63d2bfe6",
    name = "Seismic Rupture",
    setCode = "FDN",
    collectorNumber = "205",
    artist = "Jason A. Engle",
    imageUri = "https://cards.scryfall.io/normal/front/2/5/2519a51a-26a0-4884-9ba8-9db135c9ee49.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
