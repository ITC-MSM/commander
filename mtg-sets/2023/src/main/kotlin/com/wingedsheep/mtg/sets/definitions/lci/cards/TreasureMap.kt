package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Treasure Map // Treasure Cove reprint in The Lost Caverns of Ixalan (LCI). The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Ixalan's `cards/` package (the card's
 * earliest printing); this file contributes only the LCI presentation row.
 */
val TreasureMapReprint = Printing(
    oracleId = "0b55eac6-a745-4bf4-8926-5ce83bc38d7d",
    name = "Treasure Map",
    setCode = "LCI",
    collectorNumber = "267",
    scryfallId = "a924fe1e-a85e-4e14-88d2-ac55130638ab",
    artist = "Néstor Ossandón Leal",
    imageUri = "https://cards.scryfall.io/normal/front/a/9/a924fe1e-a85e-4e14-88d2-ac55130638ab.jpg?1782694398",
    backFaceImageUri = "https://cards.scryfall.io/normal/back/a/9/a924fe1e-a85e-4e14-88d2-ac55130638ab.jpg?1782694398",
    releaseDate = "2023-11-17",
    rarity = Rarity.RARE,
)
