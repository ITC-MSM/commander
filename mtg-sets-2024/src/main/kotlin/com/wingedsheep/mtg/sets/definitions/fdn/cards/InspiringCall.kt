package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Inspiring Call reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * DTK's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val InspiringCallReprint = Printing(
    oracleId = "9b9a10ff-5a5d-4df8-88aa-18d84ff9117c",
    name = "Inspiring Call",
    setCode = "FDN",
    collectorNumber = "226",
    artist = "Dan Murayama Scott",
    imageUri = "https://cards.scryfall.io/normal/front/3/e/3e241642-5172-4437-b694-f6aa159d5cd9.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
