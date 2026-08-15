package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Grizzly Bears reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * LEA's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GrizzlyBearsReprint = Printing(
    oracleId = "14c8f55d-d177-4c25-a931-ebeb9e6062a0",
    name = "Grizzly Bears",
    setCode = "POR",
    collectorNumber = "169",
    artist = "Zina Saunders",
    imageUri = "https://cards.scryfall.io/normal/front/4/8/48e1b99c-97d0-48f2-bfdf-faa65bc0b608.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.COMMON,
)
