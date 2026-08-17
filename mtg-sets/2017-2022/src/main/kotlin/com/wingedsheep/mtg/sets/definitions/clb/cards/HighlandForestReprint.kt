package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Highland Forest reprint in CLB. The canonical CardDefinition lives in
 * Kaldheim (`khm`), the card's earliest real printing; this file
 * contributes only per-printing presentation data.
 */
val HighlandForestReprint = Printing(
    oracleId = "35137378-6754-4bb1-a38e-5940890ccab1",
    name = "Highland Forest",
    setCode = "CLB",
    collectorNumber = "896",
    scryfallId = "59f64a32-c364-4750-94ed-d4d71c1a3511",
    artist = "Alayna Danner",
    imageUri = "https://cards.scryfall.io/normal/front/5/9/59f64a32-c364-4750-94ed-d4d71c1a3511.jpg?1783922375",
    releaseDate = "2022-06-10",
    rarity = Rarity.COMMON,
    frameEffects = listOf("snow"),
)
