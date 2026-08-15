package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Painful Quandary reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Scars of Mirrodin's
 * `cards/` package (its earliest real printing). This file contributes only the FDN
 * presentation row, picked up automatically by `CardDiscovery.findPrintingsIn`.
 */
val PainfulQuandaryReprint = Printing(
    oracleId = "c37051cc-6683-4dbb-b5ff-5c3a5bdab1df",
    name = "Painful Quandary",
    setCode = "FDN",
    collectorNumber = "179",
    scryfallId = "05757669-e8a6-4a2f-8479-d4e2ade822ca",
    artist = "David Palumbo",
    imageUri = "https://cards.scryfall.io/normal/front/0/5/05757669-e8a6-4a2f-8479-d4e2ade822ca.jpg?1782689112",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
