package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Felidar Cub reprint in J22. Canonical CardDefinition lives in Battle for Zendikar (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.bfz.cards.FelidarCub`.
 */
val FelidarCubReprint = Printing(
    oracleId = "cae60cf8-cd64-4595-a4dd-946694cf2bb1",
    name = "Felidar Cub",
    setCode = "J22",
    collectorNumber = "183",
    scryfallId = "b096d925-a76d-494e-8f72-ebb14263748b",
    artist = "Steve Prescott",
    imageUri = "https://cards.scryfall.io/normal/front/b/0/b096d925-a76d-494e-8f72-ebb14263748b.jpg?1783919115",
    releaseDate = "2022-12-02",
    rarity = Rarity.COMMON,
)
