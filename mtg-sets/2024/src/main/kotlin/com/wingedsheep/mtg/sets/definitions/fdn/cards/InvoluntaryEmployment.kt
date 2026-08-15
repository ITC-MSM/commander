package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Involuntary Employment reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * SNC's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val InvoluntaryEmploymentReprint = Printing(
    oracleId = "7e86fb1e-9987-43ba-b59e-0cb03bbdedb6",
    name = "Involuntary Employment",
    setCode = "FDN",
    collectorNumber = "203",
    artist = "Milivoj Ćeran",
    imageUri = "https://cards.scryfall.io/normal/front/f/3/f3ad3d62-2f24-4562-b3fa-809213dbc4a4.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
