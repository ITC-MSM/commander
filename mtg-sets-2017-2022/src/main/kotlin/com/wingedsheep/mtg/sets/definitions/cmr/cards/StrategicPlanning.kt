package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Strategic Planning reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * PTK's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val StrategicPlanningReprint = Printing(
    oracleId = "02b5acf3-47cb-4d39-9307-e02656f1879b",
    name = "Strategic Planning",
    setCode = "CMR",
    collectorNumber = "101",
    artist = "Matt Stewart",
    imageUri = "https://cards.scryfall.io/normal/front/9/1/91e14952-1a6a-4c6a-8def-54846108b542.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
