package com.wingedsheep.mtg.sets.definitions.pc2.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Three Dreams reprint in Planechase 2012. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the Ravnica: City of Guilds (`rav`)
 * `cards/` package (the card's earliest real printing); this file contributes only
 * per-printing presentation data.
 */
val ThreeDreamsReprint = Printing(
    oracleId = "b3f9da94-c822-49b8-a1fc-be82189206af",
    name = "Three Dreams",
    setCode = "PC2",
    collectorNumber = "13",
    scryfallId = "5181dab4-1256-40d4-ab86-20f366ba3b10",
    artist = "Shishizaru",
    imageUri = "https://cards.scryfall.io/normal/front/5/1/5181dab4-1256-40d4-ab86-20f366ba3b10.jpg?1783940635",
    releaseDate = "2012-06-01",
    rarity = Rarity.RARE,
)
