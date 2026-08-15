package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Spitting Earth reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * MIR's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SpittingEarthReprint = Printing(
    oracleId = "0c3bf4e1-d91e-4dd3-a800-e40971222c71",
    name = "Spitting Earth",
    setCode = "POR",
    collectorNumber = "150",
    artist = "Hannibal King",
    imageUri = "https://cards.scryfall.io/normal/front/e/b/eb16998c-cfa4-49cc-8e37-2dfc33fa2f1e.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.COMMON,
)
