package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Gifted Aetherborn reprint in Jumpstart. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * Aether Revolt's `cards/` package; this file contributes only presentation data.
 */
val GiftedAetherbornJmpReprint = Printing(
    oracleId = "0c6a6587-2b3f-40d1-a5f2-06128da0d52e",
    name = "Gifted Aetherborn",
    setCode = "JMP",
    collectorNumber = "239",
    scryfallId = "8644d4d1-8499-40a8-a01f-68172c82bf58",
    artist = "Ryan Yee",
    imageUri = "https://cards.scryfall.io/normal/front/8/6/8644d4d1-8499-40a8-a01f-68172c82bf58.jpg?1783930423",
    releaseDate = "2020-07-17",
    rarity = Rarity.UNCOMMON,
)
