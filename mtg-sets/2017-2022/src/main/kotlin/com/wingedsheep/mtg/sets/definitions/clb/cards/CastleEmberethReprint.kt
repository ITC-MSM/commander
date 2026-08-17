package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Castle Embereth reprint in CLB. The canonical CardDefinition lives in
 * Throne of Eldraine (`eld`), the card's earliest real printing; this file
 * contributes only per-printing presentation data.
 */
val CastleEmberethReprint = Printing(
    oracleId = "91fbb25b-8521-483f-88b0-77778d25f7fd",
    name = "Castle Embereth",
    setCode = "CLB",
    collectorNumber = "883",
    scryfallId = "eb4126a6-5f79-4dad-8d18-e279ca19d2b2",
    artist = "Jaime Jones",
    imageUri = "https://cards.scryfall.io/normal/front/e/b/eb4126a6-5f79-4dad-8d18-e279ca19d2b2.jpg?1783922381",
    releaseDate = "2022-06-10",
    rarity = Rarity.RARE,
)
