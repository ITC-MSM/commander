package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Frenzied Goblin reprint in FDN. Canonical CardDefinition lives in Ravnica: City of Guilds
 * (its earliest real printing), `com.wingedsheep.mtg.sets.definitions.rav.cards.FrenziedGoblin`.
 */
val FrenziedGoblinReprint = Printing(
    oracleId = "6ac470a1-c2be-4971-a5ca-10bb189ebe4d",
    name = "Frenzied Goblin",
    setCode = "FDN",
    collectorNumber = "199",
    scryfallId = "d5592573-2889-40b1-b1d5-c2802482549a",
    artist = "Randy Vargas",
    imageUri = "https://cards.scryfall.io/normal/front/d/5/d5592573-2889-40b1-b1d5-c2802482549a.jpg?1782689095",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
