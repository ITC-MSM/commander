package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Prairie Stream reprint in Innistrad: Crimson Vow Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the `bfz` `cards/` package
 * (the card's earliest real printing); this file contributes only per-printing presentation data.
 */
val PrairieStreamReprint = Printing(
    oracleId = "5330e24a-8568-446e-840a-594cd08bd1bc",
    name = "Prairie Stream",
    setCode = "VOC",
    collectorNumber = "179",
    scryfallId = "ae6bf73b-c70b-4b19-ac00-7317d5fcea7c",
    artist = "Adam Paquette",
    imageUri = "https://cards.scryfall.io/normal/front/a/e/ae6bf73b-c70b-4b19-ac00-7317d5fcea7c.jpg?1783924932",
    releaseDate = "2021-11-19",
    rarity = Rarity.RARE,
)
