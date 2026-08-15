package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Harrow reprint in Invasion.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Tempest's `cards/`
 * package (Harrow's earliest real-expansion printing); this file contributes only the
 * Invasion-specific presentation row.
 */
val HarrowReprint = Printing(
    oracleId = "705509e9-a034-4a5a-9c65-66f58748b8a2",
    name = "Harrow",
    setCode = "INV",
    collectorNumber = "189",
    scryfallId = "ed0f633e-7238-4d02-ad8b-06dd20453030",
    artist = "Rob Alexander",
    imageUri = "https://cards.scryfall.io/normal/front/e/d/ed0f633e-7238-4d02-ad8b-06dd20453030.jpg?1562942622",
    releaseDate = "2000-10-02",
    rarity = Rarity.COMMON,
)
