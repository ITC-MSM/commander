package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Hoarding Dragon reprint in Foundations.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in M11's `cards/` package
 * (the card's earliest real printing). This file contributes only the FDN presentation row.
 */
val HoardingDragonReprint = Printing(
    oracleId = "d36d3c93-ed24-4447-9d15-3071f5c0989b",
    name = "Hoarding Dragon",
    setCode = "FDN",
    collectorNumber = "626",
    scryfallId = "c3a35b3b-0c92-4b40-8210-86a5b5f275eb",
    artist = "Matt Cavotta",
    imageUri = "https://cards.scryfall.io/normal/front/c/3/c3a35b3b-0c92-4b40-8210-86a5b5f275eb.jpg?1782688720",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
