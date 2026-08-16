package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Tolarian Sentinel reprint in J22. Canonical CardDefinition lives in Time Spiral (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.tsp.cards.TolarianSentinel`.
 */
val TolarianSentinelReprint = Printing(
    oracleId = "bef15918-c0e7-4f6d-a589-f91f3fa11ef1",
    name = "Tolarian Sentinel",
    setCode = "J22",
    collectorNumber = "363",
    scryfallId = "92867aa1-698d-47cf-bed7-b758a2cc0e5d",
    artist = "Thomas M. Baxa",
    imageUri = "https://cards.scryfall.io/normal/front/9/2/92867aa1-698d-47cf-bed7-b758a2cc0e5d.jpg?1783919029",
    releaseDate = "2022-12-02",
    rarity = Rarity.COMMON,
)
