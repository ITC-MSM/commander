package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Glass Casket reprint in WOE.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * `definitions/eld/cards/GlassCasket.kt` (Throne of Eldraine is the earliest real printing);
 * this file contributes only the WOE-specific presentation row.
 */
val GlassCasketReprint = Printing(
    oracleId = "d9167046-7908-4d16-8cc1-3eb02d1ba547",
    name = "Glass Casket",
    setCode = "WOE",
    collectorNumber = "16",
    scryfallId = "02c5c395-ee8b-47fd-ac52-256354c19cdf",
    artist = "Raoul Vitale",
    imageUri = "https://cards.scryfall.io/normal/front/0/2/02c5c395-ee8b-47fd-ac52-256354c19cdf.jpg?1783915132",
    releaseDate = "2023-09-08",
    rarity = Rarity.UNCOMMON,
)
