package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Heartfire reprint in Jumpstart. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * War of the Spark's `cards/` package; this file contributes only presentation data.
 */
val HeartfireJmpReprint = Printing(
    oracleId = "2f6f5054-4a48-458e-969e-3a0f0e507354",
    name = "Heartfire",
    setCode = "JMP",
    collectorNumber = "333",
    scryfallId = "af482a14-a144-4e60-bd04-a548a3c89f5a",
    artist = "Craig J Spearing",
    imageUri = "https://cards.scryfall.io/normal/front/a/f/af482a14-a144-4e60-bd04-a548a3c89f5a.jpg?1783930390",
    releaseDate = "2020-07-17",
    rarity = Rarity.COMMON,
)
