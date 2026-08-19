package com.wingedsheep.mtg.sets.definitions.arc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Leaf Gilder reprint in Archenemy. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives
 * in another set's `cards/` package; this file contributes only presentation data.
 */
val LeafGilderArcReprint = Printing(
    oracleId = "61324e37-4b79-4325-bf46-621b4270afe2",
    name = "Leaf Gilder",
    setCode = "ARC",
    collectorNumber = "63",
    scryfallId = "2381eb3b-e45b-4fbb-a84a-0858b356a008",
    artist = "Quinton Hoover",
    imageUri = "https://cards.scryfall.io/normal/front/2/3/2381eb3b-e45b-4fbb-a84a-0858b356a008.jpg?1783941903",
    releaseDate = "2010-06-18",
    rarity = Rarity.COMMON,
)
