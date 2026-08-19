package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Wall of Blossoms reprint in Jumpstart. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * Stronghold's `cards/` package; this file contributes only presentation data.
 */
val WallOfBlossomsJmpReprint = Printing(
    oracleId = "ef4d5fb3-70a3-433d-a9d3-18b2beb8d79f",
    name = "Wall of Blossoms",
    setCode = "JMP",
    collectorNumber = "442",
    scryfallId = "f836b155-8829-460b-91f8-4cd00b988196",
    artist = "Heather Hudson",
    imageUri = "https://cards.scryfall.io/normal/front/f/8/f836b155-8829-460b-91f8-4cd00b988196.jpg?1783930349",
    releaseDate = "2020-07-17",
    rarity = Rarity.UNCOMMON,
)
