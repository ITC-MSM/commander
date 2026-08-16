package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Mistwalker reprint in J22. Canonical CardDefinition lives in Kaldheim (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.khm.cards.Mistwalker`.
 */
val MistwalkerReprint = Printing(
    oracleId = "ceb302df-d67e-4e5d-aca0-f53bcdea658d",
    name = "Mistwalker",
    setCode = "J22",
    collectorNumber = "323",
    scryfallId = "a397551c-5609-4353-aa43-504af1b27f5a",
    artist = "Steve Prescott",
    imageUri = "https://cards.scryfall.io/normal/front/a/3/a397551c-5609-4353-aa43-504af1b27f5a.jpg?1783919049",
    releaseDate = "2022-12-02",
    rarity = Rarity.COMMON,
)
