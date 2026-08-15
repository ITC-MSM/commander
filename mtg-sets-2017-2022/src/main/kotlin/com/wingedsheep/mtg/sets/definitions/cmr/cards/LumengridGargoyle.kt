package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Lumengrid Gargoyle reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * MBS's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val LumengridGargoyleReprint = Printing(
    oracleId = "1070ede7-c81d-4c77-a1a9-6f243432a640",
    name = "Lumengrid Gargoyle",
    setCode = "CMR",
    collectorNumber = "321",
    artist = "Randis Albion",
    imageUri = "https://cards.scryfall.io/normal/front/c/2/c200239c-d28e-4c55-a7bc-64a1d138cc2f.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
