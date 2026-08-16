package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Lifecrafter's Bestiary reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Aether Revolt's `cards/` package; this
 * file contributes only the New Capenna Commander presentation row.
 */
val LifecraftersBestiaryReprint = Printing(
    oracleId = "5f2a3797-28aa-4c7a-ba2b-fd243a1747fd",
    name = "Lifecrafter's Bestiary",
    setCode = "NCC",
    collectorNumber = "370",
    scryfallId = "457d1102-9ba7-47d1-bced-cc8a28e38ad9",
    artist = "Izzy",
    imageUri = "https://cards.scryfall.io/normal/front/4/5/457d1102-9ba7-47d1-bced-cc8a28e38ad9.jpg",
    releaseDate = "2022-04-29",
    rarity = Rarity.RARE,
)
