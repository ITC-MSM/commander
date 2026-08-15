package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Massacre Wurm reprint in Jumpstart 2022.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in MBS's `cards/` package
 * (the card's earliest real printing). This file contributes only the J22 presentation row.
 */
val MassacreWurmReprint = Printing(
    oracleId = "93cf50cf-0ecc-4d3e-abea-778c1ebacec4",
    name = "Massacre Wurm",
    setCode = "J22",
    collectorNumber = "441",
    scryfallId = "ee38fda1-8008-4b2d-a2b6-eb02a8b125e8",
    artist = "Jason Chan",
    imageUri = "https://cards.scryfall.io/normal/front/e/e/ee38fda1-8008-4b2d-a2b6-eb02a8b125e8.jpg?1782699079",
    releaseDate = "2022-12-02",
    rarity = Rarity.MYTHIC,
)
