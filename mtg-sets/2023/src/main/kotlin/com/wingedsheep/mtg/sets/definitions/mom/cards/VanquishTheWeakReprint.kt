package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Vanquish the Weak reprint in MOM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Ixalan (`xln`). This file
 * contributes only the MOM-specific presentation row — set, collector number, art.
 */
val VanquishTheWeakReprint = Printing(
    oracleId = "4a3cc4f6-037f-461d-a3b7-9f9e24c4b8cf",
    name = "Vanquish the Weak",
    setCode = "MOM",
    collectorNumber = "129",
    scryfallId = "935cb9a2-11ba-45d9-ab38-dea23cecf521",
    artist = "Gaboleps",
    imageUri = "https://cards.scryfall.io/normal/front/9/3/935cb9a2-11ba-45d9-ab38-dea23cecf521.jpg?1783916998",
    releaseDate = "2023-04-21",
    rarity = Rarity.COMMON,
)
