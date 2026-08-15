package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Universal Solvent reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * AER's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val UniversalSolventReprint = Printing(
    oracleId = "f34ba913-cf9a-48fe-b5fe-68bf504693b2",
    name = "Universal Solvent",
    setCode = "CMR",
    collectorNumber = "347",
    artist = "Christopher Moeller",
    imageUri = "https://cards.scryfall.io/normal/front/b/b/bbe257c5-d2f8-4a4f-bf74-f6dc4b6861e4.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
