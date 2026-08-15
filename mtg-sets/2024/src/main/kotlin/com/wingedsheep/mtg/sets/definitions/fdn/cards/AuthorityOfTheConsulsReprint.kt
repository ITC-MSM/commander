package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Authority of the Consuls reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in the KLD (Kaladesh) `cards/`
 * package — its earliest real printing. This file contributes only the FDN presentation row,
 * picked up automatically by `CardDiscovery.findPrintingsIn`.
 */
val AuthorityOfTheConsulsReprint = Printing(
    oracleId = "55f3c721-e13a-406e-bc8e-d6cdc91ac477",
    name = "Authority of the Consuls",
    setCode = "FDN",
    collectorNumber = "137",
    scryfallId = "42ce2d7f-5924-47c0-b5ed-dacf9f9617a0",
    artist = "Lake Hurwitz",
    imageUri = "https://cards.scryfall.io/normal/front/4/2/42ce2d7f-5924-47c0-b5ed-dacf9f9617a0.jpg?1782689147",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
