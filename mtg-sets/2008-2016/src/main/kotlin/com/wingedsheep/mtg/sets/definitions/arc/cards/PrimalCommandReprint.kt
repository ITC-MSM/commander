package com.wingedsheep.mtg.sets.definitions.arc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Primal Command reprint in Archenemy. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives
 * in Lorwyn's `cards/` package; this file contributes only presentation data.
 */
val PrimalCommandReprint = Printing(
    oracleId = "350f5c0a-563f-41c5-8f7b-10f409bb4d3b",
    name = "Primal Command",
    setCode = "ARC",
    collectorNumber = "66",
    scryfallId = "8a8ba5d9-9c83-45ab-aacd-ba3c44df57ea",
    artist = "Wayne England",
    imageUri = "https://cards.scryfall.io/normal/front/8/a/8a8ba5d9-9c83-45ab-aacd-ba3c44df57ea.jpg?1783941902",
    releaseDate = "2010-06-18",
    rarity = Rarity.RARE,
)
