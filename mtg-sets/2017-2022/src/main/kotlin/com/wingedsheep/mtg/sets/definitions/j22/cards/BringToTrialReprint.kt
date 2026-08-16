package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Bring to Trial reprint in J22. Canonical CardDefinition lives in Ravnica Allegiance (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.rna.cards.BringToTrial`.
 */
val BringToTrialReprint = Printing(
    oracleId = "968b6277-584c-4947-98ac-4d6e5f8d6754",
    name = "Bring to Trial",
    setCode = "J22",
    collectorNumber = "160",
    scryfallId = "444d7e10-0d5b-47f4-b29c-730d1d08a59a",
    artist = "Victor Adame Minguez",
    imageUri = "https://cards.scryfall.io/normal/front/4/4/444d7e10-0d5b-47f4-b29c-730d1d08a59a.jpg?1783919125",
    releaseDate = "2022-12-02",
    rarity = Rarity.COMMON,
)
