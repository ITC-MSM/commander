package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Silverback Shaman reprint in J22. Canonical CardDefinition lives in Core Set 2020 (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.m20.cards.SilverbackShaman`.
 */
val SilverbackShamanReprint = Printing(
    oracleId = "fc011947-b496-400e-99b8-b368068ba79b",
    name = "Silverback Shaman",
    setCode = "J22",
    collectorNumber = "728",
    scryfallId = "49463346-99b6-422f-9577-34873dd13d36",
    artist = "Mathias Kollros",
    imageUri = "https://cards.scryfall.io/normal/front/4/9/49463346-99b6-422f-9577-34873dd13d36.jpg?1783918835",
    releaseDate = "2022-12-02",
    rarity = Rarity.COMMON,
)
