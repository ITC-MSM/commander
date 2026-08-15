package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Rain of Riches reprint in Bloomburrow Commander. Canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the NCC `cards/` package; this file
 * contributes only presentation data.
 */
val RainOfRichesReprint = Printing(
    oracleId = "ef1e2d3d-e977-4729-9430-eba6242e5dfe",
    name = "Rain of Riches",
    setCode = "BLC",
    collectorNumber = "200",
    scryfallId = "299f81b5-20eb-465b-bd36-2acd80a4f37c",
    artist = "Evyn Fong",
    imageUri = "https://cards.scryfall.io/normal/front/2/9/299f81b5-20eb-465b-bd36-2acd80a4f37c.jpg?1721429174",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
