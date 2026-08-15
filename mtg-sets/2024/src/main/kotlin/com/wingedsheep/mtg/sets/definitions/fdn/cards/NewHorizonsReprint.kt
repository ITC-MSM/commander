package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * New Horizons reprint in FDN. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Ixalan (its earliest printing);
 * this file contributes only the FDN presentation row.
 */
val NewHorizonsReprint = Printing(
    oracleId = "8834d3c9-cdb5-423b-bc78-94a94add5e74",
    name = "New Horizons",
    setCode = "FDN",
    collectorNumber = "557",
    scryfallId = "86b3923c-c35c-4eb2-9dd3-b15c13778ecf",
    artist = "Eytan Zana",
    imageUri = "https://cards.scryfall.io/normal/front/8/6/86b3923c-c35c-4eb2-9dd3-b15c13778ecf.jpg?1783908946",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
