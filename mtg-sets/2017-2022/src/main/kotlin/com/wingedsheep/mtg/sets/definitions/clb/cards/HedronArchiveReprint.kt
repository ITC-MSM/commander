package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Hedron Archive reprint in CLB. The canonical CardDefinition lives in
 * Battle for Zendikar (`bfz`), the card's earliest real printing; this file
 * contributes only per-printing presentation data.
 */
val HedronArchiveReprint = Printing(
    oracleId = "32263baa-d3f0-463f-92b3-4e9938476add",
    name = "Hedron Archive",
    setCode = "CLB",
    collectorNumber = "861",
    scryfallId = "3d76d25c-b962-43e4-aa6f-7c6e3bd79f16",
    artist = "Craig J Spearing",
    imageUri = "https://cards.scryfall.io/normal/front/3/d/3d76d25c-b962-43e4-aa6f-7c6e3bd79f16.jpg?1783922390",
    releaseDate = "2022-06-10",
    rarity = Rarity.UNCOMMON,
)
