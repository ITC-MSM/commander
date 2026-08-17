package com.wingedsheep.mtg.sets.definitions.msc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Swiftfoot Boots reprint in Marvel Super Heroes Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the `m12` `cards/` package
 * (the card's earliest real printing); this file contributes only per-printing presentation data.
 */
val SwiftfootBootsReprint = Printing(
    oracleId = "c8b143ad-43ec-4e0d-a440-e348daa31391",
    name = "Swiftfoot Boots",
    setCode = "MSC",
    collectorNumber = "216",
    scryfallId = "6675632d-d74a-4b1e-8539-ac678d5545a5",
    artist = "Immanuela Crovius",
    imageUri = "https://cards.scryfall.io/normal/front/6/6/6675632d-d74a-4b1e-8539-ac678d5545a5.jpg?1783903212",
    releaseDate = "2026-06-26",
    rarity = Rarity.UNCOMMON,
)
