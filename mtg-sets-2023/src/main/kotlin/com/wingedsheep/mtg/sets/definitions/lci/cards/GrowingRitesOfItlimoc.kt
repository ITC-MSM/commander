package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Growing Rites of Itlimoc // Itlimoc, Cradle of the Sun reprint in The Lost Caverns of Ixalan
 * (LCI). The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Ixalan's `cards/`
 * package (the card's earliest printing); this file contributes only the LCI presentation row.
 */
val GrowingRitesOfItlimocReprint = Printing(
    oracleId = "ea9c459a-6047-43aa-968f-a582be4000e8",
    name = "Growing Rites of Itlimoc",
    setCode = "LCI",
    collectorNumber = "188",
    scryfallId = "004524bf-b249-4dac-9c10-44d57143feb9",
    artist = "Josu Hernaiz",
    imageUri = "https://cards.scryfall.io/normal/front/0/0/004524bf-b249-4dac-9c10-44d57143feb9.jpg?1782694458",
    backFaceImageUri = "https://cards.scryfall.io/normal/back/0/0/004524bf-b249-4dac-9c10-44d57143feb9.jpg?1782694458",
    releaseDate = "2023-11-17",
    rarity = Rarity.RARE,
)
