package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Fortify reprint in Jumpstart. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * Time Spiral's `cards/` package; this file contributes only presentation data.
 */
val FortifyJmpReprint = Printing(
    oracleId = "52ea24d4-d33d-40fc-8ffc-09ae625b908f",
    name = "Fortify",
    setCode = "JMP",
    collectorNumber = "105",
    scryfallId = "b9d4b138-5edc-4c12-b526-5c258bc1555c",
    artist = "Christopher Moeller",
    imageUri = "https://cards.scryfall.io/normal/front/b/9/b9d4b138-5edc-4c12-b526-5c258bc1555c.jpg?1783930472",
    releaseDate = "2020-07-17",
    rarity = Rarity.COMMON,
)
