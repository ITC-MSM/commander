package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Haunted Mire reprint in BLC. Canonical CardDefinition lives in DMU, the card's earliest
 * real printing; this file contributes only the BLC presentation row.
 */
val HauntedMireReprint = Printing(
    oracleId = "b0b58a03-462c-4964-97c7-42bc777ec23e",
    name = "Haunted Mire",
    setCode = "BLC",
    collectorNumber = "311",
    scryfallId = "a3ed4f23-b237-425e-9d26-f90d70713f7b",
    artist = "Bruce Brenneise",
    imageUri = "https://cards.scryfall.io/normal/front/a/3/a3ed4f23-b237-425e-9d26-f90d70713f7b.jpg?1783910637",
    releaseDate = "2024-08-02",
    rarity = Rarity.COMMON,
)
