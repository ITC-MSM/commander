package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Titanic Growth reprint in WOE.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types) lives in M12's
 * `cards/` package. This file contributes only the WOE-specific presentation row — set,
 * collector number, art — picked up automatically by `CardDiscovery.findPrintingsIn` and
 * surfaced via the set's `printings`.
 */
val TitanicGrowthReprint = Printing(
    oracleId = "61e09dd9-7870-48c2-9177-d6abc3162692",
    name = "Titanic Growth",
    setCode = "WOE",
    collectorNumber = "191",
    scryfallId = "46917de3-5e98-4dd6-8950-fc10338515df",
    artist = "Iris Compiet",
    imageUri = "https://cards.scryfall.io/normal/front/4/6/46917de3-5e98-4dd6-8950-fc10338515df.jpg?1783915076",
    releaseDate = "2023-09-08",
    rarity = Rarity.COMMON,
)
