package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Fynn, the Fangbearer reprint in FDN. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Kaldheim (its earliest printing);
 * this file contributes only the FDN presentation row.
 */
val FynnTheFangbearerReprint = Printing(
    oracleId = "c0b1fba1-4338-4671-934b-098689ad2085",
    name = "Fynn, the Fangbearer",
    setCode = "FDN",
    collectorNumber = "637",
    scryfallId = "699efb9e-2649-432b-8b2d-10775114c314",
    artist = "Lie Setiawan",
    imageUri = "https://cards.scryfall.io/normal/front/6/9/699efb9e-2649-432b-8b2d-10775114c314.jpg?1783908919",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
