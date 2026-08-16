package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Liliana's Mastery reprint in J22. Canonical CardDefinition lives in Amonkhet (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.akh.cards.LilianasMastery`.
 */
val LilianasMasteryReprint = Printing(
    oracleId = "bd104c7e-311e-4b03-98d3-5f20f3a99d26",
    name = "Liliana's Mastery",
    setCode = "J22",
    collectorNumber = "435",
    scryfallId = "e28b5fce-ac73-44ec-be2c-c95d8d3579fe",
    artist = "Kieran Yanner",
    imageUri = "https://cards.scryfall.io/normal/front/e/2/e28b5fce-ac73-44ec-be2c-c95d8d3579fe.jpg?1783918995",
    releaseDate = "2022-12-02",
    rarity = Rarity.RARE,
)
