package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Lava Dart reprint in Modern Horizons. Canonical CardDefinition lives in Judgment (its
 * earliest real printing), `com.wingedsheep.mtg.sets.definitions.jud.cards.LavaDart`.
 */
val LavaDartReprint = Printing(
    oracleId = "e48891e3-30a2-4fc8-a858-cec33c6e4ab5",
    name = "Lava Dart",
    setCode = "MH1",
    collectorNumber = "134",
    scryfallId = "b16dd041-451d-4914-8c46-aa315a90d802",
    artist = "Tyler Walpole",
    imageUri = "https://cards.scryfall.io/normal/front/b/1/b16dd041-451d-4914-8c46-aa315a90d802.jpg?1782708476",
    releaseDate = "2019-06-14",
    rarity = Rarity.COMMON,
)
