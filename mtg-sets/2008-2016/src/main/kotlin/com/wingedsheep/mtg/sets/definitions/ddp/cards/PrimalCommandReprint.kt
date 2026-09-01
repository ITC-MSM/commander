package com.wingedsheep.mtg.sets.definitions.ddp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Primal Command reprint in Duel Decks: Zendikar vs. Eldrazi. Canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Lorwyn's `cards/` package; this file
 * contributes only presentation data.
 */
val PrimalCommandReprint = Printing(
    oracleId = "350f5c0a-563f-41c5-8f7b-10f409bb4d3b",
    name = "Primal Command",
    setCode = "DDP",
    collectorNumber = "20",
    scryfallId = "87d2ad86-5d74-4507-8faa-fc6124fd5a7f",
    artist = "Magali Villeneuve",
    imageUri = "https://cards.scryfall.io/normal/front/8/7/87d2ad86-5d74-4507-8faa-fc6124fd5a7f.jpg?1783938253",
    releaseDate = "2015-08-28",
    rarity = Rarity.RARE,
)
