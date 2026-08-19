package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Winged Words reprint in Jumpstart. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * Core Set 2020's `cards/` package; this file contributes only presentation data.
 */
val WingedWordsJmpReprint = Printing(
    oracleId = "c623aeb1-e6d4-48fe-bd2a-a7a6729aa4df",
    name = "Winged Words",
    setCode = "JMP",
    collectorNumber = "196",
    scryfallId = "a48ebd79-95d7-4860-9785-45e34a94755d",
    artist = "Chris Seaman",
    imageUri = "https://cards.scryfall.io/normal/front/a/4/a48ebd79-95d7-4860-9785-45e34a94755d.jpg?1783930438",
    releaseDate = "2020-07-17",
    rarity = Rarity.COMMON,
)
