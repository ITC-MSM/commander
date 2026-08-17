package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Sunpetal Grove reprint in Ixalan. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the `m10` `cards/` package
 * (the card's earliest real printing); this file contributes only per-printing presentation data.
 */
val SunpetalGroveReprint = Printing(
    oracleId = "402ec768-76fb-474e-ae74-babc90d833c4",
    name = "Sunpetal Grove",
    setCode = "XLN",
    collectorNumber = "257",
    scryfallId = "0f234312-00e8-49f7-a489-f4c316b0a81a",
    artist = "Dimitar Marinski",
    imageUri = "https://cards.scryfall.io/normal/front/0/f/0f234312-00e8-49f7-a489-f4c316b0a81a.jpg?1783935696",
    releaseDate = "2017-09-29",
    rarity = Rarity.RARE,
)
