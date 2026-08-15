package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Rampaging Baloths reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ZEN's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RampagingBalothsReprint = Printing(
    oracleId = "2d3e6549-6cc6-434f-a189-ba3b55e64c34",
    name = "Rampaging Baloths",
    setCode = "CMR",
    collectorNumber = "431",
    artist = "Steve Prescott",
    imageUri = "https://cards.scryfall.io/normal/front/7/6/766c7781-d94e-482d-98f0-9b135ec5b8ae.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.RARE,
)
