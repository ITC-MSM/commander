package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * New Horizons reprint in WAR. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Ixalan (its earliest printing);
 * this file contributes only the WAR presentation row.
 */
val NewHorizonsReprint = Printing(
    oracleId = "8834d3c9-cdb5-423b-bc78-94a94add5e74",
    name = "New Horizons",
    setCode = "WAR",
    collectorNumber = "168",
    scryfallId = "02c521da-677e-43b9-b5fe-c84dc64e66ec",
    artist = "Eytan Zana",
    imageUri = "https://cards.scryfall.io/normal/front/0/2/02c521da-677e-43b9-b5fe-c84dc64e66ec.jpg?1783933410",
    releaseDate = "2019-05-03",
    rarity = Rarity.COMMON,
)
