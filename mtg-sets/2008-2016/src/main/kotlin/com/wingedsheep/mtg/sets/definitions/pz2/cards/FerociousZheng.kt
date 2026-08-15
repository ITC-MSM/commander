package com.wingedsheep.mtg.sets.definitions.pz2.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Ferocious Zheng reprint in PZ2.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * GS1's `cards/` package (the card's earliest real printing). This file
 * contributes only the PZ2-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val FerociousZhengReprint = Printing(
    oracleId = "a45e0854-28c7-41f5-a9fe-5b76a8070c5b",
    name = "Ferocious Zheng",
    setCode = "PZ2",
    collectorNumber = "70847",
    artist = "Yutaka Li",
    imageUri = "https://cards.scryfall.io/normal/front/0/1/012e05ec-e429-45ba-814c-3789673fe311.jpg?1783933892",
    releaseDate = "2018-12-06",
    rarity = Rarity.COMMON,
)
