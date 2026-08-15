package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Goblin Trailblazer reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * RIX's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GoblinTrailblazerReprint = Printing(
    oracleId = "38f66ef9-6afc-478b-a1d4-a9d42c31dfc9",
    name = "Goblin Trailblazer",
    setCode = "CMR",
    collectorNumber = "182",
    artist = "Josh Hass",
    imageUri = "https://cards.scryfall.io/normal/front/c/a/ca382425-2454-4300-b903-fdefd31582d3.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
