package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Swiftfoot Boots reprint in Commander Legends. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the `m12` `cards/` package
 * (the card's earliest real printing); this file contributes only per-printing presentation data.
 */
val SwiftfootBootsReprint = Printing(
    oracleId = "c8b143ad-43ec-4e0d-a440-e348daa31391",
    name = "Swiftfoot Boots",
    setCode = "CMR",
    collectorNumber = "474",
    scryfallId = "b5c45f3d-cf12-4db7-b161-9539ed969ca7",
    artist = "Svetlin Velinov",
    imageUri = "https://cards.scryfall.io/normal/front/b/5/b5c45f3d-cf12-4db7-b161-9539ed969ca7.jpg?1783928686",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
