package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Sulfur Falls reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SulfurFallsReprint = Printing(
    oracleId = "6a6c5e17-6465-4a1f-9d63-8a3ce2edc522",
    name = "Sulfur Falls",
    setCode = "BLC",
    collectorNumber = "333",
    scryfallId = "1db63651-5241-4eee-982e-5fa19db87179",
    artist = "Cliff Childs",
    imageUri = "https://cards.scryfall.io/normal/front/1/d/1db63651-5241-4eee-982e-5fa19db87179.jpg?1721429847",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
