package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Search for Tomorrow reprint in CMR. The canonical CardDefinition lives in
 * Time Spiral (`tsp`), the card's earliest real printing; this file
 * contributes only per-printing presentation data.
 */
val SearchForTomorrowReprint = Printing(
    oracleId = "9cdc9f99-c6fa-40cd-90b0-e47d43a8cc3c",
    name = "Search for Tomorrow",
    setCode = "CMR",
    collectorNumber = "436",
    scryfallId = "029b6d93-0f7a-4df4-8c01-96cbd5e03315",
    artist = "Randy Gallegos",
    imageUri = "https://cards.scryfall.io/normal/front/0/2/029b6d93-0f7a-4df4-8c01-96cbd5e03315.jpg?1783928704",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
