package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Vampiric Tutor reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * VIS's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val VampiricTutorReprint = Printing(
    oracleId = "ededbdae-d9dc-4206-9335-d7158f2d7700",
    name = "Vampiric Tutor",
    setCode = "CMR",
    collectorNumber = "156",
    artist = "Raymond Swanland",
    imageUri = "https://cards.scryfall.io/normal/front/1/8/18bd50f2-c3ba-4217-a2d5-bb771e199706.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.MYTHIC,
)
