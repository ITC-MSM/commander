package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Pilgrim's Eye reprint in J22. Canonical CardDefinition lives in Worldwake (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.wwk.cards.PilgrimsEye`.
 */
val PilgrimsEyeReprint = Printing(
    oracleId = "66155010-cc2c-449a-85d5-92c95aade514",
    name = "Pilgrim's Eye",
    setCode = "J22",
    collectorNumber = "791",
    scryfallId = "c8ec1dd3-cc5c-4bd1-b9a8-b3035e67a290",
    artist = "Dan Murayama Scott",
    imageUri = "https://cards.scryfall.io/normal/front/c/8/c8ec1dd3-cc5c-4bd1-b9a8-b3035e67a290.jpg?1783918801",
    releaseDate = "2022-12-02",
    rarity = Rarity.COMMON,
)
