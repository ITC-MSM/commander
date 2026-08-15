package com.wingedsheep.mtg.sets.definitions.c17.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Path of Ancestry reprint in C17. Canonical [com.wingedsheep.sdk.model.CardDefinition]
 * lives in PZ2 (Treasure Chest), which is technically the card's earliest printing
 * (MTGO-only redemption set, Nov 2016) — C17 is the first paper release.
 */
val PathOfAncestryReprint = Printing(
    oracleId = "b473e293-59e3-4e04-acf2-622604aeb25f",
    name = "Path of Ancestry",
    setCode = "C17",
    collectorNumber = "56",
    scryfallId = "70e70720-f0b9-4ad7-9366-927d6798d31e",
    artist = "Alayna Danner",
    imageUri = "https://cards.scryfall.io/normal/front/7/0/70e70720-f0b9-4ad7-9366-927d6798d31e.jpg?1562612293",
    releaseDate = "2017-08-25",
    rarity = Rarity.COMMON,
)
