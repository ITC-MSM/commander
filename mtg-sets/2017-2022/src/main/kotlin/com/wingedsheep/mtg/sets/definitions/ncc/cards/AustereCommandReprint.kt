package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Austere Command reprint in NCC. The canonical CardDefinition lives in Lorwyn (`lrw`), the card's
 * earliest real printing; this file contributes only per-printing presentation data.
 */
val AustereCommandReprint = Printing(
    oracleId = "09cc8709-fe10-472a-b05c-e89f3523018d",
    name = "Austere Command",
    setCode = "NCC",
    collectorNumber = "193",
    scryfallId = "a12ce59c-4b72-45a9-91df-5966d3a81f3a",
    artist = "Anna Steinbauer",
    imageUri = "https://cards.scryfall.io/normal/front/a/1/a12ce59c-4b72-45a9-91df-5966d3a81f3a.jpg?1783923295",
    releaseDate = "2022-04-29",
    rarity = Rarity.RARE,
)
