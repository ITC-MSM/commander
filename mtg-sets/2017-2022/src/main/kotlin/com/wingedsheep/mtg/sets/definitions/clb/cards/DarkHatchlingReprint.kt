package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Dark Hatchling reprint in CLB. The canonical CardDefinition lives in
 * Urza's Saga (`usg`), the card's earliest real printing; this file
 * contributes only per-printing presentation data.
 */
val DarkHatchlingReprint = Printing(
    oracleId = "36633b43-855b-4620-8afb-70c39fc07280",
    name = "Dark Hatchling",
    setCode = "CLB",
    collectorNumber = "747",
    scryfallId = "133e9654-74a3-4997-b371-7f36b5d9c4f1",
    artist = "Brad Rigney",
    imageUri = "https://cards.scryfall.io/normal/front/1/3/133e9654-74a3-4997-b371-7f36b5d9c4f1.jpg?1783922456",
    releaseDate = "2022-06-10",
    rarity = Rarity.RARE,
)
