package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Assassin's Trophy reprint in Murders at Karlov Manor. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Guilds of Ravnica's `cards/` package
 * (its earliest real-expansion printing); this file contributes only presentation data.
 */
val AssassinsTrophyReprint = Printing(
    oracleId = "ac10d218-f9a6-4058-9cda-a15ca1b0b7b5",
    name = "Assassin's Trophy",
    setCode = "MKM",
    collectorNumber = "187",
    scryfallId = "ed6c7d29-71b4-4134-b591-5598f479d592",
    artist = "Dmitry Burmak",
    imageUri = "https://cards.scryfall.io/normal/front/e/d/ed6c7d29-71b4-4134-b591-5598f479d592.jpg?1783912858",
    releaseDate = "2024-02-09",
    rarity = Rarity.RARE,
)
