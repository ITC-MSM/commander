package com.wingedsheep.mtg.sets.definitions.c15.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Izzet Signet reprint in Commander 2015. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Guildpact's `cards/` package; this file
 * contributes only the Commander 2015 presentation row.
 */
val IzzetSignetReprint = Printing(
    oracleId = "2fda4fe7-8b0c-489c-a000-6d358e614e34",
    name = "Izzet Signet",
    setCode = "C15",
    collectorNumber = "256",
    scryfallId = "f6b88bc9-375f-411f-8bb8-6d2546196fd6",
    artist = "Raoul Vitale",
    imageUri = "https://cards.scryfall.io/normal/front/f/6/f6b88bc9-375f-411f-8bb8-6d2546196fd6.jpg?1562711605",
    releaseDate = "2015-11-13",
    rarity = Rarity.COMMON,
)
