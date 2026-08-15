package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Sleight of Hand reprint in WOE.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types) lives in P02's
 * `cards/` package. This file contributes only the WOE-specific presentation row — set,
 * collector number, art — picked up automatically by `CardDiscovery.findPrintingsIn` and
 * surfaced via the set's `printings`.
 */
val SleightOfHandReprint = Printing(
    oracleId = "05a039ad-1689-4d13-b393-6c1219583b5d",
    name = "Sleight of Hand",
    setCode = "WOE",
    collectorNumber = "67",
    scryfallId = "80dea5c0-ada3-488a-9f2b-f895b92c762f",
    artist = "Scott Murphy",
    imageUri = "https://cards.scryfall.io/normal/front/8/0/80dea5c0-ada3-488a-9f2b-f895b92c762f.jpg?1783915115",
    releaseDate = "2023-09-08",
    rarity = Rarity.COMMON,
)
