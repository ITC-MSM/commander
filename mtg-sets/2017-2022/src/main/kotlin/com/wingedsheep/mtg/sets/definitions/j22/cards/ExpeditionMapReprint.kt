package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Expedition Map reprint in J22. Canonical CardDefinition lives in Zendikar (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.zen.cards.ExpeditionMap`.
 */
val ExpeditionMapReprint = Printing(
    oracleId = "8fcf50cd-e6d0-4516-850f-d42ee75dcc3a",
    name = "Expedition Map",
    setCode = "J22",
    collectorNumber = "765",
    scryfallId = "ff02f3e9-7bee-486c-9f19-9fac52c93418",
    artist = "Franz Vohwinkel",
    imageUri = "https://cards.scryfall.io/normal/front/f/f/ff02f3e9-7bee-486c-9f19-9fac52c93418.jpg?1783918812",
    releaseDate = "2022-12-02",
    rarity = Rarity.COMMON,
)
