package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Platinum Angel reprint in M11. Canonical CardDefinition lives in Mirrodin (its earliest real
 * printing), `com.wingedsheep.mtg.sets.definitions.mrd.cards.PlatinumAngel`.
 */
val PlatinumAngelM11Reprint = Printing(
    oracleId = "b148578c-c0bf-4785-b97c-4b6f83028008",
    name = "Platinum Angel",
    setCode = "M11",
    collectorNumber = "212",
    scryfallId = "a9a561bf-bddc-4ea7-bf71-9cdc07a71456",
    artist = "Brom",
    imageUri = "https://cards.scryfall.io/normal/front/a/9/a9a561bf-bddc-4ea7-bf71-9cdc07a71456.jpg?1783941789",
    releaseDate = "2010-07-16",
    rarity = Rarity.MYTHIC,
)
