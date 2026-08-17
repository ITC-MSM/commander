package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Pilgrim's Eye reprint in CLB. The canonical CardDefinition lives in
 * Worldwake (`wwk`), the card's earliest real printing; this file
 * contributes only per-printing presentation data.
 */
val PilgrimsEyeReprint = Printing(
    oracleId = "66155010-cc2c-449a-85d5-92c95aade514",
    name = "Pilgrim's Eye",
    setCode = "CLB",
    collectorNumber = "333",
    scryfallId = "32161267-e12b-454f-a7e1-94e078566ffa",
    artist = "Sean Murray",
    imageUri = "https://cards.scryfall.io/normal/front/3/2/32161267-e12b-454f-a7e1-94e078566ffa.jpg?1783922666",
    releaseDate = "2022-06-10",
    rarity = Rarity.COMMON,
)
