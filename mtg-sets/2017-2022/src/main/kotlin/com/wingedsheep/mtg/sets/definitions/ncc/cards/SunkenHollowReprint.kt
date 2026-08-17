package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Sunken Hollow reprint in NCC. The canonical CardDefinition lives in
 * Battle for Zendikar (`bfz`), the card's earliest real printing; this file
 * contributes only per-printing presentation data.
 */
val SunkenHollowReprint = Printing(
    oracleId = "cd2c90ac-2b04-461c-92f3-939871b6b6a3",
    name = "Sunken Hollow",
    setCode = "NCC",
    collectorNumber = "431",
    scryfallId = "0fb2782e-6fe9-4383-9ba4-02d21b7cb4d7",
    artist = "Adam Paquette",
    imageUri = "https://cards.scryfall.io/normal/front/0/f/0fb2782e-6fe9-4383-9ba4-02d21b7cb4d7.jpg?1783923187",
    releaseDate = "2022-04-29",
    rarity = Rarity.RARE,
)
