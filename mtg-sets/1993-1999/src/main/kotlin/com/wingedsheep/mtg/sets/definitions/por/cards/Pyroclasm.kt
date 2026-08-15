package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Pyroclasm reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ICE's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val PyroclasmReprint = Printing(
    oracleId = "e4bcd4ea-e7cd-4471-8f3b-18bb51d3d70c",
    name = "Pyroclasm",
    setCode = "POR",
    collectorNumber = "143",
    artist = "John Matson",
    imageUri = "https://cards.scryfall.io/normal/front/d/e/de214247-e5e3-4d8f-935a-797218416be1.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.RARE,
)
