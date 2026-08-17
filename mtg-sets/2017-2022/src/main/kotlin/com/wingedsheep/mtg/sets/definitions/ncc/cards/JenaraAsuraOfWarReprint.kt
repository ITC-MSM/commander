package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Jenara, Asura of War reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Alara Reborn's `cards/` package; this
 * file contributes only the New Capenna Commander presentation row.
 */
val JenaraAsuraOfWarReprint = Printing(
    oracleId = "d06cd670-7ffc-4295-97d1-0eff042fb6d5",
    name = "Jenara, Asura of War",
    setCode = "NCC",
    collectorNumber = "343",
    scryfallId = "4bcf5b5f-b908-4975-91f7-c4c6f819e0a6",
    artist = "Chris Rahn",
    imageUri = "https://cards.scryfall.io/normal/front/4/b/4bcf5b5f-b908-4975-91f7-c4c6f819e0a6.jpg",
    releaseDate = "2022-04-29",
    rarity = Rarity.MYTHIC,
)
