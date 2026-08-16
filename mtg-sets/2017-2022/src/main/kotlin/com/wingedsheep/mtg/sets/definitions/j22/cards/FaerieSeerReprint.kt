package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Faerie Seer reprint in J22. Canonical CardDefinition lives in Modern Horizons (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.mh1.cards.FaerieSeer`.
 */
val FaerieSeerReprint = Printing(
    oracleId = "b2e65e8b-5f08-4cc2-ab1d-00f8903dbea2",
    name = "Faerie Seer",
    setCode = "J22",
    collectorNumber = "295",
    scryfallId = "d05949b2-abb4-4431-b047-f4381a2a920e",
    artist = "Colin Boyer",
    imageUri = "https://cards.scryfall.io/normal/front/d/0/d05949b2-abb4-4431-b047-f4381a2a920e.jpg?1783919062",
    releaseDate = "2022-12-02",
    rarity = Rarity.COMMON,
)
