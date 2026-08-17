package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Search for Tomorrow reprint in CLB. The canonical CardDefinition lives in
 * Time Spiral (`tsp`), the card's earliest real printing; this file
 * contributes only per-printing presentation data.
 */
val SearchForTomorrowReprint = Printing(
    oracleId = "9cdc9f99-c6fa-40cd-90b0-e47d43a8cc3c",
    name = "Search for Tomorrow",
    setCode = "CLB",
    collectorNumber = "834",
    scryfallId = "4c5e2fbf-87fe-48cd-aeca-e37f0a388a30",
    artist = "Greg Staples",
    imageUri = "https://cards.scryfall.io/normal/front/4/c/4c5e2fbf-87fe-48cd-aeca-e37f0a388a30.jpg?1783922403",
    releaseDate = "2022-06-10",
    rarity = Rarity.COMMON,
)
