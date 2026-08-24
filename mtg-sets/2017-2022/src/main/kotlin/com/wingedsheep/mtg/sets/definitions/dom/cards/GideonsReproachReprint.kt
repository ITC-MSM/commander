package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Gideon's Reproach reprint in Dominaria. Canonical [com.wingedsheep.sdk.model.CardDefinition]
 * lives in the Battle for Zendikar (`bfz`) `cards/` package; this file contributes only
 * presentation data.
 */
val GideonsReproachReprint = Printing(
    oracleId = "ec43d67c-2254-4782-8bd4-3318b25907e6",
    name = "Gideon's Reproach",
    setCode = "DOM",
    collectorNumber = "19",
    scryfallId = "7b771f44-ce32-41a2-b219-738924b7f42d",
    artist = "Izzy",
    imageUri = "https://cards.scryfall.io/normal/front/7/b/7b771f44-ce32-41a2-b219-738924b7f42d.jpg?1783935051",
    releaseDate = "2018-04-27",
    rarity = Rarity.COMMON,
)
