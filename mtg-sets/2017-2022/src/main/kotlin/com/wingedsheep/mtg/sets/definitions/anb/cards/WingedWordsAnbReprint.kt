package com.wingedsheep.mtg.sets.definitions.anb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Winged Words reprint in Arena Beginner Set. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives
 * in another set's `cards/` package; this file contributes only presentation data.
 */
val WingedWordsAnbReprint = Printing(
    oracleId = "c623aeb1-e6d4-48fe-bd2a-a7a6729aa4df",
    name = "Winged Words",
    setCode = "ANB",
    collectorNumber = "43",
    scryfallId = "3449f98f-92d2-4be4-a596-fefa8843b6fd",
    artist = "Chris Seaman",
    imageUri = "https://cards.scryfall.io/normal/front/3/4/3449f98f-92d2-4be4-a596-fefa8843b6fd.jpg?1783929825",
    releaseDate = "2020-08-13",
    rarity = Rarity.COMMON,
)
