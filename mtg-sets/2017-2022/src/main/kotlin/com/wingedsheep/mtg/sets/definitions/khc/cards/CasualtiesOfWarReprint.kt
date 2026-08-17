package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Casualties of War reprint in Kaldheim Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the `war` `cards/` package
 * (the card's earliest real printing); this file contributes only per-printing presentation data.
 */
val CasualtiesOfWarReprint = Printing(
    oracleId = "5c6c8fe7-3520-423b-a224-1c0af516871a",
    name = "Casualties of War",
    setCode = "KHC",
    collectorNumber = "83",
    scryfallId = "e5a2a709-0273-48a3-874b-13aff4872b0a",
    artist = "Tomasz Jedruszek",
    imageUri = "https://cards.scryfall.io/normal/front/e/5/e5a2a709-0273-48a3-874b-13aff4872b0a.jpg?1783928305",
    releaseDate = "2021-02-05",
    rarity = Rarity.RARE,
)
