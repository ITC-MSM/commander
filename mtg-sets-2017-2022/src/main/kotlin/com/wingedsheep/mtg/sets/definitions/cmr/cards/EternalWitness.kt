package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Eternal Witness reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * 5DN's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val EternalWitnessReprint = Printing(
    oracleId = "30b24e8e-3b0e-4d8e-90f3-f66eb7c1858c",
    name = "Eternal Witness",
    setCode = "CMR",
    collectorNumber = "425",
    artist = "Chris Rahn",
    imageUri = "https://cards.scryfall.io/normal/front/d/7/d74e7ded-d063-4d90-a9ff-91c44a8098d7.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
