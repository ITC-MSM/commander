package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Swiftfoot Boots reprint in Kaldheim Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the `m12` `cards/` package
 * (the card's earliest real printing); this file contributes only per-printing presentation data.
 */
val SwiftfootBootsReprint = Printing(
    oracleId = "c8b143ad-43ec-4e0d-a440-e348daa31391",
    name = "Swiftfoot Boots",
    setCode = "KHC",
    collectorNumber = "105",
    scryfallId = "bf700ec0-1fd3-4971-ab03-51365dc8f4f4",
    artist = "Svetlin Velinov",
    imageUri = "https://cards.scryfall.io/normal/front/b/f/bf700ec0-1fd3-4971-ab03-51365dc8f4f4.jpg?1783928298",
    releaseDate = "2021-02-05",
    rarity = Rarity.UNCOMMON,
)
