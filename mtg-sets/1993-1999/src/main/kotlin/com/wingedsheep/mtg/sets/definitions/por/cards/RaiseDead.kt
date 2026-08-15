package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Raise Dead reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * LEA's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RaiseDeadReprint = Printing(
    oracleId = "cbc9c731-181a-4f00-a7b0-eb7e56eac2ea",
    name = "Raise Dead",
    setCode = "POR",
    collectorNumber = "107",
    artist = "Charles Gillespie",
    imageUri = "https://cards.scryfall.io/normal/front/e/0/e0584553-a25e-4030-ab39-53550cba3f0b.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.COMMON,
)
