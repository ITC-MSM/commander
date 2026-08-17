package com.wingedsheep.mtg.sets.definitions.msc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Sunken Hollow reprint in MSC. The canonical CardDefinition lives in
 * Battle for Zendikar (`bfz`), the card's earliest real printing; this file
 * contributes only per-printing presentation data.
 */
val SunkenHollowReprint = Printing(
    oracleId = "cd2c90ac-2b04-461c-92f3-939871b6b6a3",
    name = "Sunken Hollow",
    setCode = "MSC",
    collectorNumber = "271",
    scryfallId = "3a8eef9b-9b03-42cd-a27a-07021bf0b33f",
    artist = "Pavel Kolomeyets",
    imageUri = "https://cards.scryfall.io/normal/front/3/a/3a8eef9b-9b03-42cd-a27a-07021bf0b33f.jpg?1783903191",
    releaseDate = "2026-06-26",
    rarity = Rarity.RARE,
)
