package com.wingedsheep.mtg.sets.definitions.m13.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Glacial Fortress reprint in Magic 2013. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the `m10` `cards/` package
 * (the card's earliest real printing); this file contributes only per-printing presentation data.
 */
val GlacialFortressReprint = Printing(
    oracleId = "027dd013-baa7-4111-b3c9-f4d1414e9c45",
    name = "Glacial Fortress",
    setCode = "M13",
    collectorNumber = "225",
    scryfallId = "bc9d29ee-1a21-4c3e-99c1-f815d40e8f19",
    artist = "Franz Vohwinkel",
    imageUri = "https://cards.scryfall.io/normal/front/b/c/bc9d29ee-1a21-4c3e-99c1-f815d40e8f19.jpg?1783940458",
    releaseDate = "2012-07-13",
    rarity = Rarity.RARE,
)
