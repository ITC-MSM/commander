package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Desecration Demon reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Return to Ravnica's
 * `cards/` package (its earliest real printing). This file contributes only the FDN
 * presentation row, picked up automatically by `CardDiscovery.findPrintingsIn`.
 */
val DesecrationDemonReprint = Printing(
    oracleId = "ef75be00-1a88-47a1-a1ae-fe2c9881a798",
    name = "Desecration Demon",
    setCode = "FDN",
    collectorNumber = "603",
    scryfallId = "52bf6460-0df6-4dd3-8af8-df728683bcaa",
    artist = "Jason Chan",
    imageUri = "https://cards.scryfall.io/normal/front/5/2/52bf6460-0df6-4dd3-8af8-df728683bcaa.jpg?1782688741",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
