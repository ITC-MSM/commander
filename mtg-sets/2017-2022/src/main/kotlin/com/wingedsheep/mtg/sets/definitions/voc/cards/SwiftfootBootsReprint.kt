package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Swiftfoot Boots reprint in Innistrad: Crimson Vow Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the `m12` `cards/` package
 * (the card's earliest real printing); this file contributes only per-printing presentation data.
 */
val SwiftfootBootsReprint = Printing(
    oracleId = "c8b143ad-43ec-4e0d-a440-e348daa31391",
    name = "Swiftfoot Boots",
    setCode = "VOC",
    collectorNumber = "169",
    scryfallId = "99d75dbd-6fd2-479d-a5c7-272b4be21d8b",
    artist = "Svetlin Velinov",
    imageUri = "https://cards.scryfall.io/normal/front/9/9/99d75dbd-6fd2-479d-a5c7-272b4be21d8b.jpg?1783924937",
    releaseDate = "2021-11-19",
    rarity = Rarity.UNCOMMON,
)
