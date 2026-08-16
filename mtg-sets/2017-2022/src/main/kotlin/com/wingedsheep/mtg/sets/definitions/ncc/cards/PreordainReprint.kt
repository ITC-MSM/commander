package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Preordain reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Magic 2011's `cards/` package; this
 * file contributes only the New Capenna Commander presentation row.
 */
val PreordainReprint = Printing(
    oracleId = "ac641490-ca14-48d7-8cc4-b69ce984befa",
    name = "Preordain",
    setCode = "NCC",
    collectorNumber = "230",
    scryfallId = "d10b9be3-d4ff-4e3c-b0d5-5ab2c4e6d684",
    artist = "Svetlin Velinov",
    imageUri = "https://cards.scryfall.io/normal/front/d/1/d10b9be3-d4ff-4e3c-b0d5-5ab2c4e6d684.jpg",
    releaseDate = "2022-04-29",
    rarity = Rarity.COMMON,
)
