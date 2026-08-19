package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Wall of Vines reprint in Jumpstart. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * Magic 2011's `cards/` package; this file contributes only presentation data.
 */
val WallOfVinesJmpReprint = Printing(
    oracleId = "51ce0158-c9a5-4fa5-9704-46ab02f01b86",
    name = "Wall of Vines",
    setCode = "JMP",
    collectorNumber = "443",
    scryfallId = "9f7b7563-752b-4391-95d1-f5e3960d35c1",
    artist = "John Stanko",
    imageUri = "https://cards.scryfall.io/normal/front/9/f/9f7b7563-752b-4391-95d1-f5e3960d35c1.jpg?1783930349",
    releaseDate = "2020-07-17",
    rarity = Rarity.COMMON,
)
