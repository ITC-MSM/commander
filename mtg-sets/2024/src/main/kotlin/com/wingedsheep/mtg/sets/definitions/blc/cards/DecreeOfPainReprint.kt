package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Decree of Pain reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val DecreeOfPainReprint = Printing(
    oracleId = "932668fa-d6e3-41c0-ad0c-8e0a00e68d11",
    name = "Decree of Pain",
    setCode = "BLC",
    collectorNumber = "182",
    scryfallId = "e64e60e8-67a3-4329-8232-d0f73c09be62",
    artist = "Mathias Kollros",
    imageUri = "https://cards.scryfall.io/normal/front/e/6/e64e60e8-67a3-4329-8232-d0f73c09be62.jpg?1721429081",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
