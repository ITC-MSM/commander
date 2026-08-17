package com.wingedsheep.mtg.sets.definitions.msc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Glacial Fortress reprint in Marvel Super Heroes Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the `m10` `cards/` package
 * (the card's earliest real printing); this file contributes only per-printing presentation data.
 */
val GlacialFortressReprint = Printing(
    oracleId = "027dd013-baa7-4111-b3c9-f4d1414e9c45",
    name = "Glacial Fortress",
    setCode = "MSC",
    collectorNumber = "248",
    scryfallId = "d673a2d5-0c61-48dc-8c8d-06f0c7b6b8bf",
    artist = "Eugene Maslovski",
    imageUri = "https://cards.scryfall.io/normal/front/d/6/d673a2d5-0c61-48dc-8c8d-06f0c7b6b8bf.jpg?1783903199",
    releaseDate = "2026-06-26",
    rarity = Rarity.RARE,
)
