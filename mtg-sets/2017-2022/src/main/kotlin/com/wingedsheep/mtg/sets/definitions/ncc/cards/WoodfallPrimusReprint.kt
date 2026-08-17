package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Woodfall Primus reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Shadowmoor's `cards/` package; this
 * file contributes only the New Capenna Commander presentation row.
 */
val WoodfallPrimusReprint = Printing(
    oracleId = "2f70f1bb-29fa-4abb-afc2-653acd0a08b9",
    name = "Woodfall Primus",
    setCode = "NCC",
    collectorNumber = "322",
    scryfallId = "97914abe-71d5-4bd6-87d7-8e7379abf1aa",
    artist = "Adam Rex",
    imageUri = "https://cards.scryfall.io/normal/front/9/7/97914abe-71d5-4bd6-87d7-8e7379abf1aa.jpg",
    releaseDate = "2022-04-29",
    rarity = Rarity.RARE,
)
