package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Blinding Light reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * MIR's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BlindingLightReprint = Printing(
    oracleId = "6b315dc3-c330-4b30-b6ad-4da12ccf6ca3",
    name = "Blinding Light",
    setCode = "POR",
    collectorNumber = "8",
    artist = "John Coulthart",
    imageUri = "https://cards.scryfall.io/normal/front/4/e/4ea283d2-8f00-4836-81b4-c041b0469dcb.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.RARE,
)
