package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Mocking Sprite reprint in FDN. Canonical CardDefinition lives in its earliest set (WOE).
 */
val MockingSpriteReprint = Printing(
    oracleId = "013ca531-1ff7-4efc-a280-4bc3ab574072",
    name = "Mocking Sprite",
    setCode = "FDN",
    collectorNumber = "744",
    scryfallId = "d52d16be-c74b-4e40-94f9-2ffa497b6337",
    artist = "Ben Hill",
    imageUri = "https://cards.scryfall.io/normal/front/d/5/d52d16be-c74b-4e40-94f9-2ffa497b6337.jpg?1782683471",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
