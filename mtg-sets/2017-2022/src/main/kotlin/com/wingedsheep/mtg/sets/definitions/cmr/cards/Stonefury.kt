package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Stonefury reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * BFZ's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val StonefuryReprint = Printing(
    oracleId = "577554de-78c3-4fa2-a866-06b80280bc97",
    name = "Stonefury",
    setCode = "CMR",
    collectorNumber = "203",
    artist = "Chris Rallis",
    imageUri = "https://cards.scryfall.io/normal/front/9/c/9c111bf0-dedb-48eb-982c-f877120f12c3.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
