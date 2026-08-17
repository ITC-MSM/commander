package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Desolate Lighthouse reprint in CLB. The canonical CardDefinition lives in
 * Avacyn Restored (`avr`), the card's earliest real printing; this file
 * contributes only per-printing presentation data.
 */
val DesolateLighthouseReprint = Printing(
    oracleId = "aa6dbdf2-2379-4ff5-8a6c-70258784dc35",
    name = "Desolate Lighthouse",
    setCode = "CLB",
    collectorNumber = "890",
    scryfallId = "8b7f5239-29d3-4b2b-b464-7e43107b1348",
    artist = "Scott Chou",
    imageUri = "https://cards.scryfall.io/normal/front/8/b/8b7f5239-29d3-4b2b-b464-7e43107b1348.jpg?1783922377",
    releaseDate = "2022-06-10",
    rarity = Rarity.RARE,
)
