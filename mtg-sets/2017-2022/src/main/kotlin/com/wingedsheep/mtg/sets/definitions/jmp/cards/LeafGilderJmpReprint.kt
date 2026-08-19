package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Leaf Gilder reprint in Jumpstart. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * Lorwyn's `cards/` package; this file contributes only presentation data.
 */
val LeafGilderJmpReprint = Printing(
    oracleId = "61324e37-4b79-4325-bf46-621b4270afe2",
    name = "Leaf Gilder",
    setCode = "JMP",
    collectorNumber = "408",
    scryfallId = "58b3bd44-3b01-4507-b9be-ab94601ea736",
    artist = "Quinton Hoover",
    imageUri = "https://cards.scryfall.io/normal/front/5/8/58b3bd44-3b01-4507-b9be-ab94601ea736.jpg?1783930361",
    releaseDate = "2020-07-17",
    rarity = Rarity.COMMON,
)
