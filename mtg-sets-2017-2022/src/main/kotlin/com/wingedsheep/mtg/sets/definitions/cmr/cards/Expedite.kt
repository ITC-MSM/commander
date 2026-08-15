package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Expedite reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * OGW's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ExpediteReprint = Printing(
    oracleId = "3501a839-eef5-44e4-8637-b5754780454e",
    name = "Expedite",
    setCode = "CMR",
    collectorNumber = "413",
    artist = "Kieran Yanner",
    imageUri = "https://cards.scryfall.io/normal/front/b/8/b86c0628-0b61-44f2-91cc-1c3c309631ce.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
