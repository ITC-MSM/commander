package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Unnatural Growth reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * `definitions/mid/cards/UnnaturalGrowth.kt`. This file contributes only the
 * INR-specific presentation row.
 */
val UnnaturalGrowthReprint = Printing(
    oracleId = "7324abaa-48da-439d-9339-b0ea5eea612e",
    name = "Unnatural Growth",
    setCode = "INR",
    collectorNumber = "223",
    scryfallId = "08fa38c0-353c-4f6a-b87e-3f6366af44d8",
    artist = "Svetlin Velinov",
    imageUri = "https://cards.scryfall.io/normal/front/0/8/08fa38c0-353c-4f6a-b87e-3f6366af44d8.jpg?1736468448",
    releaseDate = "2025-01-24",
    rarity = Rarity.RARE,
)
