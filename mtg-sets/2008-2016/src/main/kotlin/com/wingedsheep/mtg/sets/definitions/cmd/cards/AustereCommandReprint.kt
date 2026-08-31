package com.wingedsheep.mtg.sets.definitions.cmd.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Austere Command reprint in CMD. The canonical CardDefinition lives in Lorwyn (`lrw`), the card's
 * earliest real printing; this file contributes only per-printing presentation data.
 */
val AustereCommandReprint = Printing(
    oracleId = "09cc8709-fe10-472a-b05c-e89f3523018d",
    name = "Austere Command",
    setCode = "CMD",
    collectorNumber = "8",
    scryfallId = "45000021-a6d9-4f86-a92e-3e52d1000c20",
    artist = "Wayne England",
    imageUri = "https://cards.scryfall.io/normal/front/4/5/45000021-a6d9-4f86-a92e-3e52d1000c20.jpg?1783941257",
    releaseDate = "2011-06-17",
    rarity = Rarity.RARE,
)
