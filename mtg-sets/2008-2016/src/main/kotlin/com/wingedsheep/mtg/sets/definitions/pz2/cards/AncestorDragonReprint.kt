package com.wingedsheep.mtg.sets.definitions.pz2.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Ancestor Dragon reprint in Treasure Chest (PZ2).
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in GS1's `cards/` package (the
 * card's earliest real printing). This file contributes only the PZ2-specific presentation row.
 */
val AncestorDragonReprint = Printing(
    oracleId = "60b03a44-a7bd-48fa-8c8f-1704aed02cd7",
    name = "Ancestor Dragon",
    setCode = "PZ2",
    collectorNumber = "70829",
    scryfallId = "423e005b-965a-4a3f-8a55-6d4371982a6e",
    artist = "Shinchuen Chen",
    imageUri = "https://cards.scryfall.io/normal/front/4/2/423e005b-965a-4a3f-8a55-6d4371982a6e.jpg?1783933899",
    releaseDate = "2018-12-06",
    rarity = Rarity.RARE,
)
