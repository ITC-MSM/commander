package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Cloudblazer reprint in Kaldheim Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the `kld` `cards/` package
 * (the card's earliest real printing); this file contributes only per-printing presentation data.
 */
val CloudblazerReprint = Printing(
    oracleId = "f84d1291-1f82-4b67-a26e-b79624b4ce1d",
    name = "Cloudblazer",
    setCode = "KHC",
    collectorNumber = "84",
    scryfallId = "edab81f0-dd20-45bd-946d-75b682e1d3d0",
    artist = "Dan Murayama Scott",
    imageUri = "https://cards.scryfall.io/normal/front/e/d/edab81f0-dd20-45bd-946d-75b682e1d3d0.jpg?1783928306",
    releaseDate = "2021-02-05",
    rarity = Rarity.UNCOMMON,
)
