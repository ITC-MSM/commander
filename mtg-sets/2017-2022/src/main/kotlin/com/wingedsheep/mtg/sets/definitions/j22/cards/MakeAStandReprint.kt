package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Make a Stand reprint in J22. Canonical CardDefinition lives in Oath of the Gatewatch (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.ogw.cards.MakeAStand`.
 */
val MakeAStandReprint = Printing(
    oracleId = "531f78d5-5004-4b02-99c7-b390cb342fd9",
    name = "Make a Stand",
    setCode = "J22",
    collectorNumber = "211",
    scryfallId = "38d0d4a3-7ce9-4881-a94b-bac5cbbf4dc0",
    artist = "Magali Villeneuve",
    imageUri = "https://cards.scryfall.io/normal/front/3/8/38d0d4a3-7ce9-4881-a94b-bac5cbbf4dc0.jpg?1783919101",
    releaseDate = "2022-12-02",
    rarity = Rarity.UNCOMMON,
)
