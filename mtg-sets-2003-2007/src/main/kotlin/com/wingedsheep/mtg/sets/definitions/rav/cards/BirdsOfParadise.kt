package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Birds of Paradise reprint in RAV.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * LEA's `cards/` package (the card's earliest real printing). This file contributes only
 * the RAV-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BirdsOfParadiseReprint = Printing(
    oracleId = "d3a0b660-358c-41bd-9cd2-41fbf3491b1a",
    name = "Birds of Paradise",
    setCode = "RAV",
    collectorNumber = "153",
    artist = "Marcelo Vignali",
    imageUri = "https://cards.scryfall.io/normal/front/9/0/90a4396a-0f22-482b-ad1d-4d9b68a1ed96.jpg",
    releaseDate = "2005-10-07",
    rarity = Rarity.RARE,
)
