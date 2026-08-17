package com.wingedsheep.mtg.sets.definitions.msc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Terramorph reprint in MSC. The canonical CardDefinition lives in
 * Modern Horizons 2 (`mh2`), the card's earliest real printing; this file
 * contributes only per-printing presentation data.
 */
val TerramorphReprint = Printing(
    oracleId = "17a34f8d-a80f-4331-8be5-06cbb9d10d7b",
    name = "Terramorph",
    setCode = "MSC",
    collectorNumber = "180",
    scryfallId = "1389d46e-956d-4be6-a519-9c8338007715",
    artist = "Eglė Mosakaitė",
    imageUri = "https://cards.scryfall.io/normal/front/1/3/1389d46e-956d-4be6-a519-9c8338007715.jpg?1783903226",
    releaseDate = "2026-06-26",
    rarity = Rarity.UNCOMMON,
)
