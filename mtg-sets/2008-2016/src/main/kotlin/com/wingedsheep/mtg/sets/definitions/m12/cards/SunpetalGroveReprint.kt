package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Sunpetal Grove reprint in Magic 2012. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the `m10` `cards/` package
 * (the card's earliest real printing); this file contributes only per-printing presentation data.
 */
val SunpetalGroveReprint = Printing(
    oracleId = "402ec768-76fb-474e-ae74-babc90d833c4",
    name = "Sunpetal Grove",
    setCode = "M12",
    collectorNumber = "229",
    scryfallId = "0c0e02be-e41f-49b4-8393-c4cd2992e380",
    artist = "Jason Chan",
    imageUri = "https://cards.scryfall.io/normal/front/0/c/0c0e02be-e41f-49b4-8393-c4cd2992e380.jpg?1783941046",
    releaseDate = "2011-07-15",
    rarity = Rarity.RARE,
)
