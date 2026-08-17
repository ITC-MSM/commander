package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Glacial Fortress reprint in Magic 2012. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the `m10` `cards/` package
 * (the card's earliest real printing); this file contributes only per-printing presentation data.
 */
val GlacialFortressReprint = Printing(
    oracleId = "027dd013-baa7-4111-b3c9-f4d1414e9c45",
    name = "Glacial Fortress",
    setCode = "M12",
    collectorNumber = "227",
    scryfallId = "8b3601d4-4091-465e-8c18-0cd717258211",
    artist = "Franz Vohwinkel",
    imageUri = "https://cards.scryfall.io/normal/front/8/b/8b3601d4-4091-465e-8c18-0cd717258211.jpg?1783941045",
    releaseDate = "2011-07-15",
    rarity = Rarity.RARE,
)
