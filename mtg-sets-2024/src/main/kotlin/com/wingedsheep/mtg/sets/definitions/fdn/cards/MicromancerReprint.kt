package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Micromancer reprint in FDN. Canonical CardDefinition lives in its earliest
 * real printing (Dominaria United, `dmu`); this file contributes only per-printing
 * presentation data.
 */
val MicromancerReprint = Printing(
    oracleId = "d2605782-09f1-4d52-b3e7-5ec1442bd9b5",
    name = "Micromancer",
    setCode = "FDN",
    collectorNumber = "158",
    scryfallId = "e6af54ea-b57a-4e50-8e46-1747cca14430",
    artist = "Ernanda Souza",
    imageUri = "https://cards.scryfall.io/normal/front/e/6/e6af54ea-b57a-4e50-8e46-1747cca14430.jpg?1782689131",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
