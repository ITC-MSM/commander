package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Storm Fleet Spy reprint in FDN. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * Ixalan (XLN); this file contributes only the FDN presentation row.
 */
val StormFleetSpyReprint = Printing(
    oracleId = "42081a57-b521-45c2-928d-0a0a4641818d",
    name = "Storm Fleet Spy",
    setCode = "FDN",
    collectorNumber = "515",
    scryfallId = "f6c5206e-63db-44c0-86ab-f645cd358b3b",
    artist = "Scott Murphy",
    imageUri = "https://cards.scryfall.io/normal/front/f/6/f6c5206e-63db-44c0-86ab-f645cd358b3b.jpg?1782688816",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
