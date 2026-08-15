package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Boros Signet reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * RAV's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BorosSignetReprint = Printing(
    oracleId = "41c84665-1f99-40ab-aaca-1188649eb263",
    name = "Boros Signet",
    setCode = "CMR",
    collectorNumber = "459",
    artist = "Mike Sass",
    imageUri = "https://cards.scryfall.io/normal/front/a/2/a2ae6081-1876-42ea-a6f8-d18dbe55c4c4.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
