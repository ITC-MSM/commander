package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Jungle Shrine reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Shards of Alara's `cards/` package; this
 * file contributes only the New Capenna Commander presentation row.
 */
val JungleShrineReprint = Printing(
    oracleId = "2e69537c-c898-4e13-a72d-ce3957a90304",
    name = "Jungle Shrine",
    setCode = "NCC",
    collectorNumber = "409",
    scryfallId = "256b65d6-f50d-4b5c-afae-77935b33f7de",
    artist = "Wayne Reynolds",
    imageUri = "https://cards.scryfall.io/normal/front/2/5/256b65d6-f50d-4b5c-afae-77935b33f7de.jpg",
    releaseDate = "2022-04-29",
    rarity = Rarity.UNCOMMON,
)
