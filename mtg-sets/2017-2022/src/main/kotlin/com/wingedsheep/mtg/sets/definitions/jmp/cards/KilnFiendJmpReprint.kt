package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Kiln Fiend reprint in Jumpstart. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * Rise of the Eldrazi's `cards/` package; this file contributes only presentation data.
 */
val KilnFiendJmpReprint = Printing(
    oracleId = "eac8c196-8477-4b79-9875-21afa1e61708",
    name = "Kiln Fiend",
    setCode = "JMP",
    collectorNumber = "338",
    scryfallId = "6c957c94-3d2d-4b98-8990-cd8909462081",
    artist = "Adi Granov",
    imageUri = "https://cards.scryfall.io/normal/front/6/c/6c957c94-3d2d-4b98-8990-cd8909462081.jpg?1783930386",
    releaseDate = "2020-07-17",
    rarity = Rarity.COMMON,
)
