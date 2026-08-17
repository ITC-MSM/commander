package com.wingedsheep.mtg.sets.definitions.msc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Prairie Stream reprint in Marvel Super Heroes Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the `bfz` `cards/` package
 * (the card's earliest real printing); this file contributes only per-printing presentation data.
 */
val PrairieStreamReprint = Printing(
    oracleId = "5330e24a-8568-446e-840a-594cd08bd1bc",
    name = "Prairie Stream",
    setCode = "MSC",
    collectorNumber = "257",
    scryfallId = "b2e133b4-2263-4ac2-8d16-7bf307d5e104",
    artist = "Rockey Chen",
    imageUri = "https://cards.scryfall.io/normal/front/b/2/b2e133b4-2263-4ac2-8d16-7bf307d5e104.jpg?1783903196",
    releaseDate = "2026-06-26",
    rarity = Rarity.RARE,
)
