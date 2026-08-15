package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Ivy Lane Denizen reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * GTC's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val IvyLaneDenizenReprint = Printing(
    oracleId = "a5da5ad6-4ed2-4041-a983-76a8c87fa109",
    name = "Ivy Lane Denizen",
    setCode = "CMR",
    collectorNumber = "236",
    artist = "Winona Nelson",
    imageUri = "https://cards.scryfall.io/normal/front/7/8/78bea375-8af3-4425-a418-bb5503e2dfb7.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
