package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Drawn from Dreams reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Core Set 2020's `cards/` package; this
 * file contributes only the New Capenna Commander presentation row.
 */
val DrawnFromDreamsReprint = Printing(
    oracleId = "77acdc13-e1b5-4a6c-9be1-b987e8256f10",
    name = "Drawn from Dreams",
    setCode = "NCC",
    collectorNumber = "220",
    scryfallId = "d61e811a-a744-40df-8d98-93f41a7bb0a0",
    artist = "Chris Seaman",
    imageUri = "https://cards.scryfall.io/normal/front/d/6/d61e811a-a744-40df-8d98-93f41a7bb0a0.jpg",
    releaseDate = "2022-04-29",
    rarity = Rarity.RARE,
)
