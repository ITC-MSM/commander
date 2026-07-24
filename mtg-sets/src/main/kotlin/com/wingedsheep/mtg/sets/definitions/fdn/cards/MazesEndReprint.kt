package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Maze's End reprint in Foundations. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Dragon's Maze
 * (`definitions/dgm/cards/MazesEnd.kt`); this file contributes only presentation data.
 */
val MazesEndReprint = Printing(
    oracleId = "49479778-c4c0-43ba-a7b7-45f00d067462",
    name = "Maze's End",
    setCode = "FDN",
    collectorNumber = "727",
    scryfallId = "ea9a4d1a-79dd-4b15-8e3b-f111f16d6bfc",
    artist = "Cliff Childs",
    imageUri = "https://cards.scryfall.io/normal/front/e/a/ea9a4d1a-79dd-4b15-8e3b-f111f16d6bfc.jpg?1783908887",
    releaseDate = "2024-11-15",
    rarity = Rarity.MYTHIC,
)
