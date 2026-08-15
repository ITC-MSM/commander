package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Captain's Call reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M13's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val CaptainsCallReprint = Printing(
    oracleId = "46418fe4-065c-4dfa-b796-eee02c14f351",
    name = "Captain's Call",
    setCode = "CMR",
    collectorNumber = "15",
    artist = "Greg Staples",
    imageUri = "https://cards.scryfall.io/normal/front/a/c/ac907330-492d-4705-bb8a-1fdb080632e1.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
