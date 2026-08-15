package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Natural Order reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * VIS's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val NaturalOrderReprint = Printing(
    oracleId = "8c1fe337-375a-4add-93b6-0ac39ed72b4f",
    name = "Natural Order",
    setCode = "POR",
    collectorNumber = "175",
    artist = "Alan Rabinowitz",
    imageUri = "https://cards.scryfall.io/normal/front/c/e/cecb34f8-6961-4c27-9368-26d156714d7b.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.RARE,
)
