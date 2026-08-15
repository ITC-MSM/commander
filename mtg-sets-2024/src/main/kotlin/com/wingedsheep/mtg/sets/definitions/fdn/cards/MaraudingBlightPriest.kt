package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Marauding Blight-Priest reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ZNR's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val MaraudingBlightPriestReprint = Printing(
    oracleId = "814b87fe-2a75-4ff2-8637-7e69e3fb285b",
    name = "Marauding Blight-Priest",
    setCode = "FDN",
    collectorNumber = "178",
    artist = "Caio Monteiro",
    imageUri = "https://cards.scryfall.io/normal/front/5/f/5f70dafc-c638-4ec0-ab5b-62998f752720.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
