package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Glacial Fortress reprint in Magic 2011. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the `m10` `cards/` package
 * (the card's earliest real printing); this file contributes only per-printing presentation data.
 */
val GlacialFortressReprint = Printing(
    oracleId = "027dd013-baa7-4111-b3c9-f4d1414e9c45",
    name = "Glacial Fortress",
    setCode = "M11",
    collectorNumber = "225",
    scryfallId = "b9d18532-2247-4e33-a760-bc42a727e9f5",
    artist = "Franz Vohwinkel",
    imageUri = "https://cards.scryfall.io/normal/front/b/9/b9d18532-2247-4e33-a760-bc42a727e9f5.jpg?1783941787",
    releaseDate = "2010-07-16",
    rarity = Rarity.RARE,
)
