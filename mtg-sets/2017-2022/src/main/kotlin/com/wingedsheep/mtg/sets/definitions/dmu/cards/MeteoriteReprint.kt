package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Meteorite reprint in DMU. The canonical [com.wingedsheep.sdk.model.CardDefinition]
 * lives in Magic 2015's `cards/` package; this file contributes only presentation data.
 */
val MeteoriteReprint = Printing(
    oracleId = "0c3c3bcc-d485-4a01-92fe-8bf29c0fc926",
    name = "Meteorite",
    setCode = "DMU",
    collectorNumber = "235",
    scryfallId = "33eb2032-50af-4fd6-bdc7-7cae2211956c",
    artist = "Olena Richards",
    imageUri = "https://cards.scryfall.io/normal/front/3/3/33eb2032-50af-4fd6-bdc7-7cae2211956c.jpg?1783921269",
    releaseDate = "2022-09-09",
    rarity = Rarity.COMMON,
)
