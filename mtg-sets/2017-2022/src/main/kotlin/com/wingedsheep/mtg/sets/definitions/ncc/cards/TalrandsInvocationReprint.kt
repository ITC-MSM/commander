package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Talrand's Invocation reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Magic 2013's `cards/` package; this
 * file contributes only the New Capenna Commander presentation row.
 */
val TalrandsInvocationReprint = Printing(
    oracleId = "43355ac4-bf8c-48f6-a322-fafbc9d132d1",
    name = "Talrand's Invocation",
    setCode = "NCC",
    collectorNumber = "234",
    scryfallId = "88f43c2c-fee4-4df2-b326-a1d9840f64b0",
    artist = "Svetlin Velinov",
    imageUri = "https://cards.scryfall.io/normal/front/8/8/88f43c2c-fee4-4df2-b326-a1d9840f64b0.jpg",
    releaseDate = "2022-04-29",
    rarity = Rarity.UNCOMMON,
)
