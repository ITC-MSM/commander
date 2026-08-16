package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Shadowmage Infiltrator reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Odyssey's `cards/` package; this
 * file contributes only the New Capenna Commander presentation row.
 */
val ShadowmageInfiltratorReprint = Printing(
    oracleId = "3a6f886b-2043-47e9-9c0f-f7913a6fa67d",
    name = "Shadowmage Infiltrator",
    setCode = "NCC",
    collectorNumber = "351",
    scryfallId = "c2ac3ee0-3adf-4e81-9194-cb0e9faf2826",
    artist = "Tomasz Jedruszek",
    imageUri = "https://cards.scryfall.io/normal/front/c/2/c2ac3ee0-3adf-4e81-9194-cb0e9faf2826.jpg",
    releaseDate = "2022-04-29",
    rarity = Rarity.RARE,
)
