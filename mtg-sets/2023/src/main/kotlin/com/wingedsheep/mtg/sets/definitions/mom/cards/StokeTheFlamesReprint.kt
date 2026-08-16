package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Stoke the Flames reprint in MOM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Magic 2015 (`m15`). This file
 * contributes only the MOM-specific presentation row — set, collector number, art.
 */
val StokeTheFlamesReprint = Printing(
    oracleId = "2249001c-08d5-4c0a-86f8-d97519a39f37",
    name = "Stoke the Flames",
    setCode = "MOM",
    collectorNumber = "166",
    scryfallId = "04113b3c-cc8f-4b15-9091-f82ea3df2e7c",
    artist = "Liiga Smilshkalne",
    imageUri = "https://cards.scryfall.io/normal/front/0/4/04113b3c-cc8f-4b15-9091-f82ea3df2e7c.jpg?1783916979",
    releaseDate = "2023-04-21",
    rarity = Rarity.UNCOMMON,
)
