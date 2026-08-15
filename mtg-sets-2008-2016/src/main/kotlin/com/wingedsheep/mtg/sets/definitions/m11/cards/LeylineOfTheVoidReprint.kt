package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Leyline of the Void reprint in Magic 2011 (M11 #101). The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Guildpact's `cards/` package
 * ([com.wingedsheep.mtg.sets.definitions.gpt.cards.LeylineOfTheVoid], the earliest real-expansion
 * printing); this file contributes only M11 presentation data.
 */
val LeylineOfTheVoidReprint = Printing(
    oracleId = "f4e32fc1-1b8d-441e-8e76-71f19f98e925",
    name = "Leyline of the Void",
    setCode = "M11",
    collectorNumber = "101",
    scryfallId = "e012d4f3-c387-4910-981b-7532fd355296",
    artist = "Rob Alexander",
    imageUri = "https://cards.scryfall.io/normal/front/e/0/e012d4f3-c387-4910-981b-7532fd355296.jpg?1562478565",
    releaseDate = "2010-07-16",
    rarity = Rarity.RARE,
)
