package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Swiftblade Vindicator reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * GRN's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SwiftbladeVindicatorReprint = Printing(
    oracleId = "44240b1e-c104-4eb4-8ab9-0d56607ba241",
    name = "Swiftblade Vindicator",
    setCode = "FDN",
    collectorNumber = "246",
    artist = "Viktor Titov",
    imageUri = "https://cards.scryfall.io/normal/front/f/9/f94618ec-000c-4371-b925-05ff82bfe221.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
