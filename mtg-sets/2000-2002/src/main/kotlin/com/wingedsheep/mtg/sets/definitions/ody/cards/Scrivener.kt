package com.wingedsheep.mtg.sets.definitions.ody.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Scrivener reprint in ODY.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * EXO's `cards/` package (the card's earliest real printing). This file contributes only
 * the ODY-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ScrivenerReprint = Printing(
    oracleId = "27c37a8d-777a-400d-b096-bde195228ea8",
    name = "Scrivener",
    setCode = "ODY",
    collectorNumber = "100",
    artist = "Kev Walker",
    imageUri = "https://cards.scryfall.io/normal/front/6/0/606f16fb-0829-45f9-a12e-aeb2371dd533.jpg",
    releaseDate = "2001-10-01",
    rarity = Rarity.COMMON,
)
