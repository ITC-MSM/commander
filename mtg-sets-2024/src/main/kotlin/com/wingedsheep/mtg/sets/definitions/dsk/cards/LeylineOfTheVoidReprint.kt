package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Leyline of the Void reprint in Duskmourn: House of Horror (DSK #106). The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Guildpact's `cards/` package
 * ([com.wingedsheep.mtg.sets.definitions.gpt.cards.LeylineOfTheVoid], the earliest real-expansion
 * printing); this file contributes only DSK presentation data.
 */
val LeylineOfTheVoidReprint = Printing(
    oracleId = "f4e32fc1-1b8d-441e-8e76-71f19f98e925",
    name = "Leyline of the Void",
    setCode = "DSK",
    collectorNumber = "106",
    scryfallId = "aeaa3aff-608d-4723-bb7c-8daedebe9f36",
    artist = "Sergey Glushakov",
    imageUri = "https://cards.scryfall.io/normal/front/a/e/aeaa3aff-608d-4723-bb7c-8daedebe9f36.jpg?1726286244",
    releaseDate = "2024-09-27",
    rarity = Rarity.RARE,
)
