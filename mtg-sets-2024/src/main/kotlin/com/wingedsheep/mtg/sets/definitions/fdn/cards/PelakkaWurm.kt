package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Pelakka Wurm reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ROE's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val PelakkaWurmReprint = Printing(
    oracleId = "d36075c2-de66-4202-9217-b1102a2bc14b",
    name = "Pelakka Wurm",
    setCode = "FDN",
    collectorNumber = "765",
    artist = "Daniel Ljunggren",
    imageUri = "https://cards.scryfall.io/normal/front/3/0/304c635c-de4d-46ee-8ab0-e5c4d55b61b3.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
