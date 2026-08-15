package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Fleeting Distraction reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ROE's `cards/` package (the card's earliest real printing). This file contributes only
 * the FDN-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val FleetingDistractionReprint = Printing(
    oracleId = "b36271cb-cda2-434a-a580-6050dc460409",
    name = "Fleeting Distraction",
    setCode = "FDN",
    collectorNumber = "155",
    artist = "Ryan Yee",
    imageUri = "https://cards.scryfall.io/normal/front/c/0/c0b86a7b-4912-43a7-ab89-c3432385baa1.jpg",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
