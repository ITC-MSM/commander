package com.wingedsheep.mtg.sets.definitions.anb.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Krovikan Scoundrel reprint in ANB.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * CSP's `cards/` package (the card's earliest real printing). This file
 * contributes only the ANB-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val KrovikanScoundrelReprint = Printing(
    oracleId = "44c437ca-5d36-4bb3-9450-baf0c93ff4ce",
    name = "Krovikan Scoundrel",
    setCode = "ANB",
    collectorNumber = "50",
    artist = "Ralph Horsley",
    imageUri = "https://cards.scryfall.io/normal/front/1/b/1b672836-dba8-4a32-ac04-616644268534.jpg?1783929827",
    releaseDate = "2020-08-13",
    rarity = Rarity.COMMON,
)
