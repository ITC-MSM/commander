package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Sea Gate Oracle reprint in Jumpstart. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * Rise of the Eldrazi's `cards/` package; this file contributes only presentation data.
 */
val SeaGateOracleJmpReprint = Printing(
    oracleId = "b333c194-d285-4dfb-984c-57b7e88393af",
    name = "Sea Gate Oracle",
    setCode = "JMP",
    collectorNumber = "173",
    scryfallId = "3a0b3006-16cb-4752-908e-3c9f37ac249c",
    artist = "Daniel Ljunggren",
    imageUri = "https://cards.scryfall.io/normal/front/3/a/3a0b3006-16cb-4752-908e-3c9f37ac249c.jpg?1783930448",
    releaseDate = "2020-07-17",
    rarity = Rarity.COMMON,
)
