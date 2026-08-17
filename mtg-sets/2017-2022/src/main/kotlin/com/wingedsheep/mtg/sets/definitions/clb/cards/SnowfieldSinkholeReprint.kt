package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Snowfield Sinkhole reprint in CLB. The canonical CardDefinition lives in
 * Kaldheim (`khm`), the card's earliest real printing; this file
 * contributes only per-printing presentation data.
 */
val SnowfieldSinkholeReprint = Printing(
    oracleId = "749c2c8e-9588-4e83-b07f-3c37eb63338b",
    name = "Snowfield Sinkhole",
    setCode = "CLB",
    collectorNumber = "915",
    scryfallId = "3c6e17f2-b1e4-4189-a02f-92fa4b13a1ed",
    artist = "Marta Nael",
    imageUri = "https://cards.scryfall.io/normal/front/3/c/3c6e17f2-b1e4-4189-a02f-92fa4b13a1ed.jpg?1783922367",
    releaseDate = "2022-06-10",
    rarity = Rarity.COMMON,
    frameEffects = listOf("snow"),
)
