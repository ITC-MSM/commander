package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Ryusei, the Falling Star reprint in CLB. The canonical CardDefinition lives in
 * Champions of Kamigawa (`chk`), the card's earliest real printing; this file
 * contributes only per-printing presentation data.
 */
val RyuseiTheFallingStarReprint = Printing(
    oracleId = "5e99ba3b-dc2b-4c3c-84ce-228d4772cfee",
    name = "Ryusei, the Falling Star",
    setCode = "CLB",
    collectorNumber = "806",
    scryfallId = "68d57ab8-fbe0-4f01-b7f7-2a5cccd2d13a",
    artist = "Grzegorz Rutkowski",
    imageUri = "https://cards.scryfall.io/normal/front/6/8/68d57ab8-fbe0-4f01-b7f7-2a5cccd2d13a.jpg?1783922416",
    releaseDate = "2022-06-10",
    rarity = Rarity.RARE,
    frameEffects = listOf("legendary"),
)
