package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Encroaching Wastes reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M14's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val EncroachingWastesReprint = Printing(
    oracleId = "43144f06-079b-4515-a03a-01ea3e90d586",
    name = "Encroaching Wastes",
    setCode = "CMR",
    collectorNumber = "481",
    artist = "Noah Bradley",
    imageUri = "https://cards.scryfall.io/normal/front/a/5/a591bffd-2f03-48f4-a719-04f2142abd77.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
