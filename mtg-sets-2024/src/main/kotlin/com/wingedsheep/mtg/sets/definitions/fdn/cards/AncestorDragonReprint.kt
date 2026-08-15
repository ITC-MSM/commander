package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Ancestor Dragon reprint in Foundations.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * GS1's `cards/` package (the card's earliest real printing). This file contributes only the
 * FDN-specific presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val AncestorDragonReprint = Printing(
    oracleId = "60b03a44-a7bd-48fa-8c8f-1704aed02cd7",
    name = "Ancestor Dragon",
    setCode = "FDN",
    collectorNumber = "489",
    scryfallId = "df43d9d4-3fef-4a56-8b38-d52824117201",
    artist = "Shinchuen Chen",
    imageUri = "https://cards.scryfall.io/normal/front/d/f/df43d9d4-3fef-4a56-8b38-d52824117201.jpg?1783908968",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
