package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Lathliss, Dragon Queen reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M19's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val LathlissDragonQueenReprint = Printing(
    oracleId = "15fef45d-f1a9-49b2-abaa-fe77bb9d1afd",
    name = "Lathliss, Dragon Queen",
    setCode = "FDN",
    collectorNumber = "627",
    artist = "Alex Konstad",
    imageUri = "https://cards.scryfall.io/normal/front/4/0/409acb8f-cf03-4a56-a8c0-e4c97a01ee10.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
