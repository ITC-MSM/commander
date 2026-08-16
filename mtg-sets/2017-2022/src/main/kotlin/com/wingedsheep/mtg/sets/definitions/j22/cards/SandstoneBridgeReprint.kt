package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Sandstone Bridge reprint in J22. Canonical CardDefinition lives in Battle for Zendikar (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.bfz.cards.SandstoneBridge`.
 */
val SandstoneBridgeReprint = Printing(
    oracleId = "08911e8e-cd67-4960-a927-958c33632469",
    name = "Sandstone Bridge",
    setCode = "J22",
    collectorNumber = "819",
    scryfallId = "ffb3d37e-5118-405a-906d-529ec1d90dce",
    artist = "Cliff Childs",
    imageUri = "https://cards.scryfall.io/normal/front/f/f/ffb3d37e-5118-405a-906d-529ec1d90dce.jpg?1783918788",
    releaseDate = "2022-12-02",
    rarity = Rarity.COMMON,
)
