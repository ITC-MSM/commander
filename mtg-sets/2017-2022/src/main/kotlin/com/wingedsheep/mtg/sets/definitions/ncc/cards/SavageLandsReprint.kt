package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Savage Lands reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Shards of Alara's `cards/` package; this
 * file contributes only the New Capenna Commander presentation row.
 */
val SavageLandsReprint = Printing(
    oracleId = "a3292406-3f49-42d6-a547-e43dd5797f84",
    name = "Savage Lands",
    setCode = "NCC",
    collectorNumber = "424",
    scryfallId = "05bd2945-af12-4f36-b2b3-47d766af521e",
    artist = "Vance Kovacs",
    imageUri = "https://cards.scryfall.io/normal/front/0/5/05bd2945-af12-4f36-b2b3-47d766af521e.jpg",
    releaseDate = "2022-04-29",
    rarity = Rarity.UNCOMMON,
)
