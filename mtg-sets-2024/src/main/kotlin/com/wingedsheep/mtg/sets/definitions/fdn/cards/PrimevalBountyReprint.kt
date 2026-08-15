package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Primeval Bounty reprint in Foundations.
 *
 * Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in the M14 `cards/`
 * package; this file contributes only the FDN-specific presentation row.
 */
val PrimevalBountyReprint = Printing(
    oracleId = "f633c16d-d943-421c-ad18-af35db9ec9fc",
    name = "Primeval Bounty",
    setCode = "FDN",
    collectorNumber = "644",
    scryfallId = "332c9742-dc3b-48e5-8736-7724fae1b4c4",
    artist = "Christine Choi",
    imageUri = "https://cards.scryfall.io/normal/front/3/3/332c9742-dc3b-48e5-8736-7724fae1b4c4.jpg?1730491040",
    releaseDate = "2024-11-15",
    rarity = Rarity.MYTHIC,
)
