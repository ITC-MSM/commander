package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * New Horizons reprint in JMP. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Ixalan (its earliest printing);
 * this file contributes only the JMP presentation row.
 */
val NewHorizonsReprint = Printing(
    oracleId = "8834d3c9-cdb5-423b-bc78-94a94add5e74",
    name = "New Horizons",
    setCode = "JMP",
    collectorNumber = "414",
    scryfallId = "f7df29a3-c40f-4cd8-a6fe-c1b95085cfed",
    artist = "Noah Bradley",
    imageUri = "https://cards.scryfall.io/normal/front/f/7/f7df29a3-c40f-4cd8-a6fe-c1b95085cfed.jpg?1783930359",
    releaseDate = "2020-07-17",
    rarity = Rarity.COMMON,
)
