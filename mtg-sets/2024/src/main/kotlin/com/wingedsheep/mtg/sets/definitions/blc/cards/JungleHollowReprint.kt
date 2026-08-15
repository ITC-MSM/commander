package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Jungle Hollow reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val JungleHollowReprint = Printing(
    oracleId = "6de714e1-446d-4fb9-9e3d-bcd3ec6af9ca",
    name = "Jungle Hollow",
    setCode = "BLC",
    collectorNumber = "313",
    scryfallId = "5401ef4c-b1e8-4f37-abe8-25c405d4f6ea",
    artist = "Thomas Stoop",
    imageUri = "https://cards.scryfall.io/normal/front/5/4/5401ef4c-b1e8-4f37-abe8-25c405d4f6ea.jpg?1721429764",
    releaseDate = "2024-08-02",
    rarity = Rarity.COMMON,
)
