package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Leyline of the Void reprint in Core Set 2020 (M20 #107). The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Guildpact's `cards/` package
 * ([com.wingedsheep.mtg.sets.definitions.gpt.cards.LeylineOfTheVoid], the earliest real-expansion
 * printing); this file contributes only M20 presentation data.
 */
val LeylineOfTheVoidReprint = Printing(
    oracleId = "f4e32fc1-1b8d-441e-8e76-71f19f98e925",
    name = "Leyline of the Void",
    setCode = "M20",
    collectorNumber = "107",
    scryfallId = "04d5d429-e0c6-42cc-a477-da7dabb1c295",
    artist = "Noah Bradley",
    imageUri = "https://cards.scryfall.io/normal/front/0/4/04d5d429-e0c6-42cc-a477-da7dabb1c295.jpg?1592516724",
    releaseDate = "2019-07-12",
    rarity = Rarity.RARE,
)
