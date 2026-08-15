package com.wingedsheep.mtg.sets.definitions.big.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Torpor Orb reprint in The Big Score (BIG).
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in NPH's `cards/` package
 * (the card's earliest real printing). This file contributes only the BIG presentation row.
 */
val TorporOrbReprint = Printing(
    oracleId = "97326cad-b13c-4e52-82ce-850a39e5ff08",
    name = "Torpor Orb",
    setCode = "BIG",
    collectorNumber = "27",
    scryfallId = "dbf02a38-d10d-463e-ab99-e7fd848a1bd3",
    artist = "Robin Olausson",
    imageUri = "https://cards.scryfall.io/normal/front/d/b/dbf02a38-d10d-463e-ab99-e7fd848a1bd3.jpg?1770090426",
    releaseDate = "2024-04-19",
    rarity = Rarity.MYTHIC,
)
