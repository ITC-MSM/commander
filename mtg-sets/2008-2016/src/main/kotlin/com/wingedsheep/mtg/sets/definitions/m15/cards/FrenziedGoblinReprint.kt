package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Frenzied Goblin reprint in M15. Canonical CardDefinition lives in Ravnica: City of Guilds
 * (its earliest real printing), `com.wingedsheep.mtg.sets.definitions.rav.cards.FrenziedGoblin`.
 */
val FrenziedGoblinM15Reprint = Printing(
    oracleId = "6ac470a1-c2be-4971-a5ca-10bb189ebe4d",
    name = "Frenzied Goblin",
    setCode = "M15",
    collectorNumber = "142",
    scryfallId = "7ddfe382-3a80-45f3-a022-54739c4b69a6",
    artist = "Carl Critchlow",
    imageUri = "https://cards.scryfall.io/normal/front/7/d/7ddfe382-3a80-45f3-a022-54739c4b69a6.jpg?1782713201",
    releaseDate = "2014-07-18",
    rarity = Rarity.UNCOMMON,
)
