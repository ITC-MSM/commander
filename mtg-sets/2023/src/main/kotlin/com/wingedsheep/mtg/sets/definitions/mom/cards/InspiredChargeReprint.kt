package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Inspired Charge reprint in MOM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Magic 2011 (`m11`). This file
 * contributes only the MOM-specific presentation row — set, collector number, art.
 */
val InspiredChargeReprint = Printing(
    oracleId = "d465a3d6-5830-456c-8e7a-908b464db846",
    name = "Inspired Charge",
    setCode = "MOM",
    collectorNumber = "19",
    scryfallId = "9f17e624-219a-4e76-bfe0-f49c9ddd4a6d",
    artist = "Vladimir Krisetskiy",
    imageUri = "https://cards.scryfall.io/normal/front/9/f/9f17e624-219a-4e76-bfe0-f49c9ddd4a6d.jpg?1783917060",
    releaseDate = "2023-04-21",
    rarity = Rarity.COMMON,
)
