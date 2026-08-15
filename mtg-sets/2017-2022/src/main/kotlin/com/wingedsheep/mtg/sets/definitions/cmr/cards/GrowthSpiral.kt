package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Growth Spiral reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * RNA's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GrowthSpiralReprint = Printing(
    oracleId = "34bcc217-dd91-45a0-90d7-a94d02f1f317",
    name = "Growth Spiral",
    setCode = "CMR",
    collectorNumber = "446",
    artist = "Seb McKinnon",
    imageUri = "https://cards.scryfall.io/normal/front/5/c/5c0f0add-4ed5-4146-972f-ece8a19e567d.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
