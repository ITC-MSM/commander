package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Nullpriest of Oblivion reprint in Foundations.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in ZNR's
 * `cards/` package (the card's earliest real printing). This file contributes only the
 * FDN-specific presentation row.
 */
val NullpriestOfOblivionReprint = Printing(
    oracleId = "c1111228-ef88-4b64-91e0-66ee32a1f5e4",
    name = "Nullpriest of Oblivion",
    setCode = "FDN",
    collectorNumber = "611",
    scryfallId = "b2c7613c-38fc-49c3-93ee-1df93136455a",
    artist = "Yongjae Choi",
    imageUri = "https://cards.scryfall.io/normal/front/b/2/b2c7613c-38fc-49c3-93ee-1df93136455a.jpg?1783908928",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
