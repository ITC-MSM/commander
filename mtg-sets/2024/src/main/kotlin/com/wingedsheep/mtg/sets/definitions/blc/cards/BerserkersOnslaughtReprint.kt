package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Berserkers' Onslaught reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types) lives in
 * `definitions/dtk/cards/BerserkersOnslaught.kt`. This file contributes only the
 * BLC-specific presentation row — set, collector number, art — picked up
 * automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's
 * `printings`.
 */
val BerserkersOnslaughtReprint = Printing(
    oracleId = "85d2948c-1a79-418d-80c4-bc1012a4d313",
    name = "Berserkers' Onslaught",
    setCode = "BLC",
    collectorNumber = "192",
    scryfallId = "83c64ec3-ad6d-45d8-a44a-fd557d1ad2bf",
    artist = "Zoltan Boros",
    imageUri = "https://cards.scryfall.io/normal/front/8/3/83c64ec3-ad6d-45d8-a44a-fd557d1ad2bf.jpg?1721429132",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
