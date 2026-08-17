package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Selfless Spirit reprint in Commander Legends: Battle for Baldur's Gate. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the `emn` `cards/` package
 * (the card's earliest real printing); this file contributes only per-printing presentation data.
 */
val SelflessSpiritReprint = Printing(
    oracleId = "71d785a9-ddc8-472e-a778-a551b444a4bd",
    name = "Selfless Spirit",
    setCode = "CLB",
    collectorNumber = "706",
    scryfallId = "ef8d8d0c-cb0e-4745-a0fb-d556c9324428",
    artist = "Seb McKinnon",
    imageUri = "https://cards.scryfall.io/normal/front/e/f/ef8d8d0c-cb0e-4745-a0fb-d556c9324428.jpg?1783922481",
    releaseDate = "2022-06-10",
    rarity = Rarity.RARE,
)
