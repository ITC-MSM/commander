package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Dwynen's Elite reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ORI's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DwynensEliteReprint = Printing(
    oracleId = "3d4a7dd3-9258-4bd1-adc4-08f0205e196a",
    name = "Dwynen's Elite",
    setCode = "FDN",
    collectorNumber = "218",
    artist = "Volkan Baǵa",
    imageUri = "https://cards.scryfall.io/normal/front/8/9/89d94c28-ea2e-4a3d-935f-6b2d9f2efc7a.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
