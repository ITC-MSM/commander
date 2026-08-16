package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Faerie Formation reprint in J22. Canonical CardDefinition lives in Throne of Eldraine (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.eld.cards.FaerieFormation`.
 */
val FaerieFormationReprint = Printing(
    oracleId = "0366c2f6-6e78-4526-808c-fa7bace6006e",
    name = "Faerie Formation",
    setCode = "J22",
    collectorNumber = "294",
    scryfallId = "86682959-6720-44e0-8fe3-982f0b3e94ce",
    artist = "Ryan Yee",
    imageUri = "https://cards.scryfall.io/normal/front/8/6/86682959-6720-44e0-8fe3-982f0b3e94ce.jpg?1783919062",
    releaseDate = "2022-12-02",
    rarity = Rarity.RARE,
)
