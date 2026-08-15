package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Massacre Wurm reprint in Foundations.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in MBS's `cards/` package
 * (the card's earliest real printing). This file contributes only the FDN presentation row —
 * set, collector number, art — picked up automatically by `CardDiscovery.findPrintingsIn`.
 */
val MassacreWurmReprint = Printing(
    oracleId = "93cf50cf-0ecc-4d3e-abea-778c1ebacec4",
    name = "Massacre Wurm",
    setCode = "FDN",
    collectorNumber = "714",
    scryfallId = "39be2675-986c-4812-9448-99737e797671",
    artist = "Jason Chan",
    imageUri = "https://cards.scryfall.io/normal/front/3/9/39be2675-986c-4812-9448-99737e797671.jpg?1782688645",
    releaseDate = "2024-11-15",
    rarity = Rarity.MYTHIC,
)
