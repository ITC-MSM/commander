package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Staff of Domination reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * 5DN's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val StaffOfDominationReprint = Printing(
    oracleId = "d7888719-647d-4022-a211-822fa09f0791",
    name = "Staff of Domination",
    setCode = "CMR",
    collectorNumber = "343",
    artist = "Ben Thompson",
    imageUri = "https://cards.scryfall.io/normal/front/f/d/fde838c8-2f32-4e7d-a236-0bc42dd7abd9.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.RARE,
)
