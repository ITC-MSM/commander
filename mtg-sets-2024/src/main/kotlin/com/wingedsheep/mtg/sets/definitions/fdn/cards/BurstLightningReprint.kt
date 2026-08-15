package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Burst Lightning reprint in FDN. Canonical CardDefinition lives in Zendikar (its
 * earliest real printing), `com.wingedsheep.mtg.sets.definitions.zen.cards.BurstLightning`.
 */
val BurstLightningReprint = Printing(
    oracleId = "ac2086fe-98ee-4280-9c7c-c5c2d6548a8b",
    name = "Burst Lightning",
    setCode = "FDN",
    collectorNumber = "192",
    scryfallId = "aec5d380-d354-4750-931a-6c91853e2edc",
    artist = "Vance Kovacs",
    imageUri = "https://cards.scryfall.io/normal/front/a/e/aec5d380-d354-4750-931a-6c91853e2edc.jpg?1782689101",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
