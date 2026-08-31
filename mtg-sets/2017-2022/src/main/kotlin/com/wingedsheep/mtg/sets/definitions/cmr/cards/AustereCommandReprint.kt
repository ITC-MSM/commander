package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Austere Command reprint in CMR. The canonical CardDefinition lives in Lorwyn (`lrw`), the card's
 * earliest real printing; this file contributes only per-printing presentation data.
 */
val AustereCommandReprint = Printing(
    oracleId = "09cc8709-fe10-472a-b05c-e89f3523018d",
    name = "Austere Command",
    setCode = "CMR",
    collectorNumber = "12",
    scryfallId = "ce4ec853-411d-40a3-84a7-a62b3cb57cb3",
    artist = "Anna Steinbauer",
    imageUri = "https://cards.scryfall.io/normal/front/c/e/ce4ec853-411d-40a3-84a7-a62b3cb57cb3.jpg?1783928889",
    releaseDate = "2020-11-20",
    rarity = Rarity.RARE,
)
