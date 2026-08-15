package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Court Street Denizen reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * GTC's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val CourtStreetDenizenReprint = Printing(
    oracleId = "34312179-f3f9-47ca-bdf4-5abbd493876e",
    name = "Court Street Denizen",
    setCode = "CMR",
    collectorNumber = "17",
    artist = "Volkan Baǵa",
    imageUri = "https://cards.scryfall.io/normal/front/c/4/c45b1d32-5a3a-4ec3-af11-d0ba947de175.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
