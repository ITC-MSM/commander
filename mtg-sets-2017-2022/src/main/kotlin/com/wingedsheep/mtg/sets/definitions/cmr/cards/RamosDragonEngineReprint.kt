package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Ramos, Dragon Engine reprint in Commander Legends. The canonical card definition lives in Commander 2017.
 */
val RamosDragonEngineReprint = Printing(
    oracleId = "3ed41d2d-211b-4013-8562-8c64d54cc43a",
    name = "Ramos, Dragon Engine",
    setCode = "CMR",
    collectorNumber = "545",
    scryfallId = "5865c39e-847e-4dc6-8e6c-a4200124f21f",
    artist = "Joseph Meehan",
    imageUri = "https://cards.scryfall.io/normal/front/5/8/5865c39e-847e-4dc6-8e6c-a4200124f21f.jpg?1783928658",
    releaseDate = "2020-11-20",
    rarity = Rarity.MYTHIC,
    frameEffects = listOf("inverted", "etched", "legendary")
)
