package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Swiftwater Cliffs reprint in BLB.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLB-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SwiftwaterCliffsReprint = Printing(
    oracleId = "2f4ad084-2062-44c0-9975-15f100204531",
    name = "Swiftwater Cliffs",
    setCode = "BLB",
    collectorNumber = "397",
    scryfallId = "89e43510-c444-4b2e-b2a0-528dcc09c899",
    artist = "Eytan Zana",
    imageUri = "https://cards.scryfall.io/normal/front/8/9/89e43510-c444-4b2e-b2a0-528dcc09c899.jpg?1721428123",
    releaseDate = "2024-08-02",
    rarity = Rarity.COMMON,
)
