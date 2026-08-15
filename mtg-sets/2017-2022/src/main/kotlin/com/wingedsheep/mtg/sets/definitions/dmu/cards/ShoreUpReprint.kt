package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Shore Up reprint in DMU.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the DMU-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ShoreUpReprint = Printing(
    oracleId = "c9d8c983-2845-42e6-b6b8-bf7cf2729b23",
    name = "Shore Up",
    setCode = "DMU",
    collectorNumber = "64",
    scryfallId = "9d933bf1-14f0-4150-a0d2-6b845b9624cf",
    artist = "Mark Behm",
    imageUri = "https://cards.scryfall.io/normal/front/9/d/9d933bf1-14f0-4150-a0d2-6b845b9624cf.jpg?1673306836",
    releaseDate = "2022-09-09",
    rarity = Rarity.COMMON,
)
