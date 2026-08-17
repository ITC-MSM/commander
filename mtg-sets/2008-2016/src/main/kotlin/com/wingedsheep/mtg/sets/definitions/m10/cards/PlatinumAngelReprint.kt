package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Platinum Angel reprint in M10. Canonical CardDefinition lives in Mirrodin (its earliest real
 * printing), `com.wingedsheep.mtg.sets.definitions.mrd.cards.PlatinumAngel`.
 */
val PlatinumAngelM10Reprint = Printing(
    oracleId = "b148578c-c0bf-4785-b97c-4b6f83028008",
    name = "Platinum Angel",
    setCode = "M10",
    collectorNumber = "218",
    scryfallId = "7b782865-b9d1-41ed-8a7b-a36c17022190",
    artist = "Brom",
    imageUri = "https://cards.scryfall.io/normal/front/7/b/7b782865-b9d1-41ed-8a7b-a36c17022190.jpg?1783942354",
    releaseDate = "2009-07-17",
    rarity = Rarity.MYTHIC,
)
