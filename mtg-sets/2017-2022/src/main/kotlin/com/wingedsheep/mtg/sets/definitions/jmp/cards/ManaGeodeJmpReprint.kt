package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Mana Geode reprint in Jumpstart. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * War of the Spark's `cards/` package; this file contributes only presentation data.
 */
val ManaGeodeJmpReprint = Printing(
    oracleId = "0844f4e6-2b98-4d09-a5c5-92f0a3b6a517",
    name = "Mana Geode",
    setCode = "JMP",
    collectorNumber = "472",
    scryfallId = "f8c54d41-683e-42fd-8aa4-371dddf3bcb3",
    artist = "Raoul Vitale",
    imageUri = "https://cards.scryfall.io/normal/front/f/8/f8c54d41-683e-42fd-8aa4-371dddf3bcb3.jpg?1783930338",
    releaseDate = "2020-07-17",
    rarity = Rarity.COMMON,
)
