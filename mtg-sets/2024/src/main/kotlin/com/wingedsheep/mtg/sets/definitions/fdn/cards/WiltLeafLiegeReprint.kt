package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Wilt-Leaf Liege reprint in FDN. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * Shadowmoor (SHM); this file contributes only the FDN presentation row.
 */
val WiltLeafLiegeReprint = Printing(
    oracleId = "2441696b-a9ba-4813-ba2e-e71f85281d05",
    name = "Wilt-Leaf Liege",
    setCode = "FDN",
    collectorNumber = "668",
    scryfallId = "4d383bbf-6bb1-4c2c-899b-65d8df9d0889",
    artist = "Jason Chan",
    imageUri = "https://cards.scryfall.io/normal/front/4/d/4d383bbf-6bb1-4c2c-899b-65d8df9d0889.jpg?1783908908",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
