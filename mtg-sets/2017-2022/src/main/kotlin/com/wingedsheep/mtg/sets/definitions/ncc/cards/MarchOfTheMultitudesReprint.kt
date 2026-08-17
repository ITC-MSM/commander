package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * March of the Multitudes reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Guilds of Ravnica's `cards/` package; this
 * file contributes only the New Capenna Commander presentation row.
 */
val MarchOfTheMultitudesReprint = Printing(
    oracleId = "0c26ab0d-80f6-4e5b-9d0e-af17c1519583",
    name = "March of the Multitudes",
    setCode = "NCC",
    collectorNumber = "346",
    scryfallId = "65c7067d-61ec-4558-b0d4-0048d2d86743",
    artist = "Zack Stella",
    imageUri = "https://cards.scryfall.io/normal/front/6/5/65c7067d-61ec-4558-b0d4-0048d2d86743.jpg",
    releaseDate = "2022-04-29",
    rarity = Rarity.MYTHIC,
)
