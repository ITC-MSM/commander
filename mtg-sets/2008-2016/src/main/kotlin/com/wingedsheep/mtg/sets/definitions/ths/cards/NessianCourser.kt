package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Nessian Courser reprint in THS.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (types, P/T) lives in
 * FUT's `cards/` package (the card's earliest real printing). This file
 * contributes only the THS-specific presentation row — set, collector number, art —
 * picked up automatically by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val NessianCourserReprint = Printing(
    oracleId = "e876d1fc-3acd-41a3-a34b-2bfa83204393",
    name = "Nessian Courser",
    setCode = "THS",
    collectorNumber = "165",
    artist = "Steve Prescott",
    imageUri = "https://cards.scryfall.io/normal/front/4/6/4697f3aa-abde-4379-af82-f30115f59be0.jpg?1783939743",
    releaseDate = "2013-09-27",
    rarity = Rarity.COMMON,
)
