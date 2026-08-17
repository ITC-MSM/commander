package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Lightning Bolt reprint in CLB. The canonical CardDefinition lives in
 * Limited Edition Alpha (`lea`), the card's earliest real printing; this file
 * contributes only per-printing presentation data.
 */
val LightningBoltReprint = Printing(
    oracleId = "4457ed35-7c10-48c8-9776-456485fdf070",
    name = "Lightning Bolt",
    setCode = "CLB",
    collectorNumber = "187",
    scryfallId = "ae5f9fb1-5a55-4db3-98a1-2628e3598c18",
    artist = "Irina Nordsol",
    imageUri = "https://cards.scryfall.io/normal/front/a/e/ae5f9fb1-5a55-4db3-98a1-2628e3598c18.jpg?1783922736",
    releaseDate = "2022-06-10",
    rarity = Rarity.COMMON,
)
