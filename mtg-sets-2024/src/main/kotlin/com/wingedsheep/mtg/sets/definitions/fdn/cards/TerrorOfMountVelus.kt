package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Terror of Mount Velus reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * THB's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TerrorOfMountVelusReprint = Printing(
    oracleId = "8161248c-16a3-47db-a3ae-cdfd878d49e0",
    name = "Terror of Mount Velus",
    setCode = "FDN",
    collectorNumber = "764",
    artist = "Billy Christian",
    imageUri = "https://cards.scryfall.io/normal/front/4/0/4047b1c3-06eb-400d-993d-69b5210977ee.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
