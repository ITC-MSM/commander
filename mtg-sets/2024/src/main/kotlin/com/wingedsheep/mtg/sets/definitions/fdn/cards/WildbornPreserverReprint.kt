package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Wildborn Preserver reprint in FDN. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Throne of Eldraine (its earliest printing);
 * this file contributes only the FDN presentation row.
 */
val WildbornPreserverReprint = Printing(
    oracleId = "55cec4bc-3de6-4c0d-a4cc-c5a30849fbda",
    name = "Wildborn Preserver",
    setCode = "FDN",
    collectorNumber = "650",
    scryfallId = "8a249d1c-a5fe-48e5-bd9b-e50d8eea391b",
    artist = "Lius Lasahido",
    imageUri = "https://cards.scryfall.io/normal/front/8/a/8a249d1c-a5fe-48e5-bd9b-e50d8eea391b.jpg?1783908914",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
