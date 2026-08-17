package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Swiftfoot Boots reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Magic 2012's `cards/` package; this
 * file contributes only the New Capenna Commander presentation row.
 */
val SwiftfootBootsReprint = Printing(
    oracleId = "c8b143ad-43ec-4e0d-a440-e348daa31391",
    name = "Swiftfoot Boots",
    setCode = "NCC",
    collectorNumber = "382",
    scryfallId = "3cb171ef-42eb-466e-b425-e3c16301c0ca",
    artist = "Svetlin Velinov",
    imageUri = "https://cards.scryfall.io/normal/front/3/c/3cb171ef-42eb-466e-b425-e3c16301c0ca.jpg",
    releaseDate = "2022-04-29",
    rarity = Rarity.UNCOMMON,
)
