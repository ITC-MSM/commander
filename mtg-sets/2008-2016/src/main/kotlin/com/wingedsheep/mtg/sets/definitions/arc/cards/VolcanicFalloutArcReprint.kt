package com.wingedsheep.mtg.sets.definitions.arc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Volcanic Fallout reprint in Archenemy. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives
 * in another set's `cards/` package; this file contributes only presentation data.
 */
val VolcanicFalloutArcReprint = Printing(
    oracleId = "bd6ce147-c991-40ac-b276-65b630fad8b6",
    name = "Volcanic Fallout",
    setCode = "ARC",
    collectorNumber = "51",
    scryfallId = "9e2c51bc-ebc9-4264-9426-122e7f568bd1",
    artist = "Zoltan Boros & Gabor Szikszai",
    imageUri = "https://cards.scryfall.io/normal/front/9/e/9e2c51bc-ebc9-4264-9426-122e7f568bd1.jpg?1783941905",
    releaseDate = "2010-06-18",
    rarity = Rarity.UNCOMMON,
)
