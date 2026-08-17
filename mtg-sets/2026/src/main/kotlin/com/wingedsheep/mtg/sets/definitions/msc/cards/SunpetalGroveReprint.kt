package com.wingedsheep.mtg.sets.definitions.msc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Sunpetal Grove reprint in Marvel Super Heroes Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the `m10` `cards/` package
 * (the card's earliest real printing); this file contributes only per-printing presentation data.
 */
val SunpetalGroveReprint = Printing(
    oracleId = "402ec768-76fb-474e-ae74-babc90d833c4",
    name = "Sunpetal Grove",
    setCode = "MSC",
    collectorNumber = "272",
    scryfallId = "e83092ee-4a90-4eac-915f-3fd01b7d9bd0",
    artist = "Rafater",
    imageUri = "https://cards.scryfall.io/normal/front/e/8/e83092ee-4a90-4eac-915f-3fd01b7d9bd0.jpg?1783903191",
    releaseDate = "2026-06-26",
    rarity = Rarity.RARE,
)
