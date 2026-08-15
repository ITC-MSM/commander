package com.wingedsheep.mtg.sets.definitions.por.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Archangel reprint in POR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * VIS's `cards/` package (the card's earliest real printing). This file contributes only
 * the POR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ArchangelReprint = Printing(
    oracleId = "9971697b-2acc-4bc2-a44e-074d03a51df7",
    name = "Archangel",
    setCode = "POR",
    collectorNumber = "3",
    artist = "Quinton Hoover",
    imageUri = "https://cards.scryfall.io/normal/front/3/8/387b9236-1241-44b7-9436-1fbc9970b692.jpg",
    releaseDate = "1997-05-01",
    rarity = Rarity.RARE,
)
